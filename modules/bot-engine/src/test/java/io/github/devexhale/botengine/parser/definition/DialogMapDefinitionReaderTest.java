package io.github.devexhale.botengine.parser.definition;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.devexhale.botengine.domain.dialog.DialogMap;
import io.github.devexhale.botengine.domain.dialog.DialogNode;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DialogMapDefinitionReaderTest {

  private static final String NODE_ID = "start_node";

  private DialogMapDefinitionReader reader;

  @BeforeEach
  void setUp() {
    reader = new DialogMapDefinitionReader();
  }

  @Test
  void targetType_shouldReturnDialogMapClass_whenCalled() {
    Class<DialogMap> result = reader.targetType();

    assertEquals(DialogMap.class, result);
  }

  @Test
  void rawType_shouldReturnNonNullTypeReference_whenCalled() {
    TypeReference<Map<String, DialogNode>> result = reader.rawType();

    assertNotNull(result);
  }

  @Test
  void map_shouldReturnDialogMapContainingGivenNodes_whenCalled() {
    DialogNode node = mock(DialogNode.class);
    Map<String, DialogNode> rawMap = Map.of(NODE_ID, node);

    DialogMap result = reader.map(rawMap);

    assertNotNull(result);
    assertEquals(node, result.nodes().get(NODE_ID));
  }
}
