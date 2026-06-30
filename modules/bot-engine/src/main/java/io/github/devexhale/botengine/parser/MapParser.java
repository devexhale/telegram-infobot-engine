package io.github.devexhale.botengine.parser;

import io.github.devexhale.botengine.diagnostics.exception.DefinitionInitializationException;
import io.github.devexhale.botengine.parser.definition.MapDefinitionReader;
import io.github.devexhale.botengine.parser.definition.MapDefinitionReaderRegistry;
import io.github.devexhale.botengine.parser.format.ConfigFormatReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Parses configuration files into typed domain objects.
 *
 * <p>Delegates format-specific reading to {@link ConfigFormatReader} implementations and type
 * mapping to {@link MapDefinitionReader} implementations.
 *
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MapParser {

  private final List<ConfigFormatReader> formatReaders;
  private final MapDefinitionReaderRegistry definitionReaderRegistry;

  /**
   * Parses the input stream into the specified target type.
   *
   * @param fileName the name of the file being parsed
   * @param inputStream the input stream to read
   * @param targetType the target domain class
   * @param <T> the target type
   * @return the parsed domain object
   * @throws DefinitionInitializationException if parsing fails
   */
  public <T> T parse(String fileName, InputStream inputStream, Class<T> targetType) {
    ConfigFormatReader formatReader = resolveFormatReader(fileName);
    MapDefinitionReader<T, ?> definitionReader = definitionReaderRegistry.get(targetType);

    try {
      T result = parseInternal(formatReader, definitionReader, inputStream);
      log.info(
          "Parsed file '{}' into '{}' using format reader '{}' and definition reader '{}'",
          fileName,
          targetType.getSimpleName(),
          formatReader.getClass().getSimpleName(),
          definitionReader.getClass().getSimpleName());
      return result;
    } catch (IOException e) {
      throw new DefinitionInitializationException(
          "Failed to parse file '%s' into '%s'. Check the correct syntax and structure of your definition config file."
              .formatted(fileName, targetType.getSimpleName()),
          e);
    }
  }

  private ConfigFormatReader resolveFormatReader(String fileName) {
    return formatReaders.stream()
        .filter(reader -> reader.supports(fileName))
        .findFirst()
        .orElseThrow(
            () ->
                new DefinitionInitializationException(
                    "No config format reader found for file '%s'".formatted(fileName)));
  }

  private <T, R> T parseInternal(
      ConfigFormatReader formatReader,
      MapDefinitionReader<T, R> definitionReader,
      InputStream inputStream)
      throws IOException {
    R raw = formatReader.read(inputStream, definitionReader.rawType());
    return definitionReader.map(raw);
  }
}
