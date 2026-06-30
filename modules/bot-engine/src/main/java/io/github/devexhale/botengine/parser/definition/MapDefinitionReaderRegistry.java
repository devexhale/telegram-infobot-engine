package io.github.devexhale.botengine.parser.definition;

import io.github.devexhale.botengine.diagnostics.exception.DefinitionInitializationException;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * Registry for {@link MapDefinitionReader} implementations.
 *
 * <p>Maps target domain classes to their corresponding readers.
 *
 * @since 1.0
 */
@Component
public class MapDefinitionReaderRegistry {

  private final Map<Class<?>, MapDefinitionReader<?, ?>> readersByType;

  /**
   * Initializes the registry with the provided readers.
   *
   * @param readers the list of readers to register
   */
  public MapDefinitionReaderRegistry(List<MapDefinitionReader<?, ?>> readers) {
    readersByType =
        readers.stream()
            .collect(
                Collectors.toUnmodifiableMap(MapDefinitionReader::targetType, Function.identity()));
  }

  /**
   * Retrieves the reader for the specified target type.
   *
   * @param targetType the target domain class
   * @param <T> the target type
   * @param <R> the raw parsed type
   * @return the registered reader
   * @throws DefinitionInitializationException if no reader is found for the type
   */
  @SuppressWarnings("unchecked")
  public <T, R> MapDefinitionReader<T, R> get(Class<T> targetType) {
    MapDefinitionReader<?, ?> reader = readersByType.get(targetType);

    if (reader == null) {
      throw new DefinitionInitializationException(
          "No map definition reader registered for type '%s'"
              .formatted(targetType.getSimpleName()));
    }

    return (MapDefinitionReader<T, R>) reader;
  }
}
