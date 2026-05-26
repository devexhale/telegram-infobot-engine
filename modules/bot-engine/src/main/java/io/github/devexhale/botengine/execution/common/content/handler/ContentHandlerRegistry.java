package io.github.devexhale.botengine.execution.common.content.handler;

import io.github.devexhale.botengine.domain.content.ContentType;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;

@Component
public class ContentHandlerRegistry {

  private final Map<ContentType, ContentHandler> handlers = new EnumMap<>(ContentType.class);

  public ContentHandlerRegistry(List<ContentHandler> contentHandlers) {
    for (ContentHandler handler : contentHandlers) {
      if (handlers.putIfAbsent(handler.type(), handler) != null) {
        throw new IllegalStateException("Duplicate handler for type: " + handler.type());
      }
    }
  }

  public Optional<ContentHandler> get(ContentType type) {
    return Optional.ofNullable(handlers.get(type));
  }
}
