package io.github.devexhale.botengine.parser.definition;

import io.github.devexhale.botengine.diagnostics.exception.DefinitionInitializationException;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class MapDefinitionReaderRegistry {

  private final Map<Class<?>, MapDefinitionReader<?, ?>> readersByType;

  public MapDefinitionReaderRegistry(List<MapDefinitionReader<?, ?>> readers) {
    this.readersByType =
        readers.stream()
            .collect(
                Collectors.toUnmodifiableMap(MapDefinitionReader::targetType, Function.identity()));
  }

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
