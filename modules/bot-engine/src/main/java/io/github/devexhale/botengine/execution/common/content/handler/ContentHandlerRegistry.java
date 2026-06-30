package io.github.devexhale.botengine.execution.common.content.handler;

import io.github.devexhale.botengine.domain.content.ContentType;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;

/**
 * Registry for {@link ContentHandler} implementations.
 *
 * <p>Maps each {@link ContentType} to its corresponding handler and ensures no duplicate handlers
 * are registered.
 *
 * @since 1.0
 */
@Component
public class ContentHandlerRegistry {

  private final Map<ContentType, ContentHandler> handlers = new EnumMap<>(ContentType.class);

  /**
   * Initializes the registry with the provided handlers.
   *
   * @param contentHandlers the list of handlers to register
   * @throws IllegalStateException if a duplicate handler is found
   */
  public ContentHandlerRegistry(List<ContentHandler> contentHandlers) {
    for (ContentHandler handler : contentHandlers) {
      if (handlers.putIfAbsent(handler.type(), handler) != null) {
        throw new IllegalStateException("Duplicate handler for type: " + handler.type());
      }
    }
  }

  /**
   * Retrieves the handler for the specified content type.
   *
   * @param type the content type to look up
   * @return an {@link Optional} containing the handler, or empty if not found
   */
  public Optional<ContentHandler> get(ContentType type) {
    return Optional.ofNullable(handlers.get(type));
  }
}
