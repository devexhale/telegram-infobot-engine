package io.github.devexhale.botengine.parser.definition;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.devexhale.botengine.domain.broadcast.BroadcastMap;
import io.github.devexhale.botengine.domain.broadcast.BroadcastNode;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BroadcastMapDefinitionReaderTest {

  private static final String NODE_ID = "broadcast_node";

  private BroadcastMapDefinitionReader reader;

  @BeforeEach
  void setUp() {
    reader = new BroadcastMapDefinitionReader();
  }

  @Test
  void targetType_shouldReturnBroadcastMapClass_whenCalled() {
    Class<BroadcastMap> result = reader.targetType();

    assertEquals(BroadcastMap.class, result);
  }

  @Test
  void rawType_shouldReturnNonNullTypeReference_whenCalled() {
    TypeReference<Map<String, BroadcastNode>> result = reader.rawType();

    assertNotNull(result);
  }

  @Test
  void map_shouldReturnBroadcastMapContainingGivenNodes_whenCalled() {
    BroadcastNode node = mock(BroadcastNode.class);
    Map<String, BroadcastNode> rawMap = Map.of(NODE_ID, node);

    BroadcastMap result = reader.map(rawMap);

    assertNotNull(result);
    assertEquals(node, result.nodes().get(NODE_ID));
  }
}
