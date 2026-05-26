package io.github.devexhale.botengine.loader;

import io.github.devexhale.botengine.diagnostics.exception.DefinitionInitializationException;
import io.github.devexhale.botengine.parser.MapParser;
import io.github.devexhale.botengine.validator.definition.DefinitionValidator;
import io.github.devexhale.botengine.validator.definition.DefinitionValidatorRegistry;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DefinitionLoader {

  private final MapParser mapParser;
  private final DefinitionValidatorRegistry validatorRegistry;
  private final ResourceLoader resourceLoader;

  public <T> T load(String fileName, Class<T> targetType) {
    String resourcePath = Paths.get(fileName).toString();
    Resource resource = resourceLoader.getResource(resourcePath);

    if (!resource.exists()) {
      throw new DefinitionInitializationException(
          "Definition config file '%s' not found".formatted(fileName));
    }

    try (InputStream inputStream = resource.getInputStream()) {
      byte[] content = inputStream.readAllBytes();
      checkNotEmpty(content, fileName);

      T config = mapParser.parse(fileName, new ByteArrayInputStream(content), targetType);

      DefinitionValidator<T> validator = validatorRegistry.get(targetType);

      if (validator != null) {
        validator.validate(config, fileName);
      }

      return config;
    } catch (IOException e) {
      throw new DefinitionInitializationException(
          "Failed to load definition config file '%s' as '%s'"
              .formatted(fileName, targetType.getSimpleName()),
          e);
    }
  }

  private void checkNotEmpty(byte[] content, String fileName) {
    if (content.length == 0) {
      throw new DefinitionInitializationException(
          "Definition config file '%s' is empty".formatted(fileName));
    }
  }
}
