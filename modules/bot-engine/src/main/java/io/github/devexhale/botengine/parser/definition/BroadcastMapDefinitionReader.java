package io.github.devexhale.botengine.parser.definition;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.devexhale.botengine.annotation.ConditionalOnBroadcastEnabled;
import io.github.devexhale.botengine.domain.broadcast.BroadcastMap;
import io.github.devexhale.botengine.domain.broadcast.BroadcastNode;

import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * Reads raw broadcast node maps and maps them to a {@link BroadcastMap} domain object.
 *
 * @since 1.0
 */
@Component
@ConditionalOnBroadcastEnabled
public class BroadcastMapDefinitionReader
    implements MapDefinitionReader<BroadcastMap, Map<String, BroadcastNode>> {

  private static final TypeReference<Map<String, BroadcastNode>> RAW_TYPE =
      new TypeReference<>() {};

  @Override
  public Class<BroadcastMap> targetType() {
    return BroadcastMap.class;
  }

  @Override
  public TypeReference<Map<String, BroadcastNode>> rawType() {
    return RAW_TYPE;
  }

  @Override
  public BroadcastMap map(Map<String, BroadcastNode> raw) {
    return new BroadcastMap(raw);
  }
}
