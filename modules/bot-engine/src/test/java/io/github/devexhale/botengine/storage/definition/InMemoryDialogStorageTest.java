package io.github.devexhale.botengine.storage.definition;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import io.github.devexhale.botengine.domain.dialog.DialogMap;
import io.github.devexhale.botengine.domain.dialog.DialogNode;
import io.github.devexhale.botengine.loader.DefinitionLoader;
import io.github.devexhale.botengine.properties.DialogProperties;
import io.github.devexhale.botengine.testsupport.logger.TestLogCaptor;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InMemoryDialogStorageTest {

  private static final String FILE_NAME = "dialog.yaml";
  private static final String NODE_ID = "test_node";

  @Mock private DialogProperties properties;
  @Mock private DefinitionLoader loader;

  @InjectMocks private InMemoryDialogStorage dialogStorage;

  @Mock private DialogMap dialogMap;
  @Mock private DialogNode dialogNode;

  @BeforeEach
  void setUp() {
    lenient().when(properties.fileName()).thenReturn(FILE_NAME);
  }

  @Test
  void warmUp_shouldLoadDialogMapAndLogSuccess_whenCalled() {
    when(loader.load(FILE_NAME, DialogMap.class)).thenReturn(dialogMap);

    try (TestLogCaptor logCaptor = new TestLogCaptor(InMemoryDialogStorage.class)) {
      dialogStorage.warmUp();

      verify(loader).load(FILE_NAME, DialogMap.class);

      ILoggingEvent startLog = logCaptor.events().getFirst();
      assertEquals(Level.INFO, startLog.getLevel());
      assertTrue(startLog.getFormattedMessage().contains("Warming up dialog cache"));
      assertTrue(startLog.getFormattedMessage().contains(FILE_NAME));

      ILoggingEvent successLog = logCaptor.events().get(1);
      assertEquals(Level.INFO, successLog.getLevel());
      assertTrue(successLog.getFormattedMessage().contains("warmed up successfully"));
      assertTrue(successLog.getFormattedMessage().contains(FILE_NAME));
    }
  }

  @Test
  void isEnabled_shouldReturnTrue_whenCalled() {
    assertTrue(dialogStorage.isEnabled());
  }

  @Test
  void getNode_shouldReturnNodeFromHolder_whenWarmedUp() {
    when(loader.load(FILE_NAME, DialogMap.class)).thenReturn(dialogMap);
    when(dialogMap.getNode(NODE_ID)).thenReturn(dialogNode);

    dialogStorage.warmUp();

    DialogNode result = dialogStorage.getNode(NODE_ID);

    assertEquals(dialogNode, result);
  }

  @Test
  void getMap_shouldReturnNodesMapFromHolder_whenWarmedUp() {
    Map<String, DialogNode> expectedMap = Map.of(NODE_ID, dialogNode);
    when(loader.load(FILE_NAME, DialogMap.class)).thenReturn(dialogMap);
    when(dialogMap.nodes()).thenReturn(expectedMap);

    dialogStorage.warmUp();

    Map<String, DialogNode> result = dialogStorage.getMap();

    assertEquals(expectedMap, result);
  }
}
