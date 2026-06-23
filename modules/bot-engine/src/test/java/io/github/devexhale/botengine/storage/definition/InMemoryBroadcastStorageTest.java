package io.github.devexhale.botengine.storage.definition;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import io.github.devexhale.botengine.domain.broadcast.BroadcastMap;
import io.github.devexhale.botengine.domain.broadcast.BroadcastNode;
import io.github.devexhale.botengine.loader.DefinitionLoader;
import io.github.devexhale.botengine.properties.BroadcastProperties;
import io.github.devexhale.botengine.testsupport.logger.TestLogCaptor;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InMemoryBroadcastStorageTest {

  private static final String FILE_NAME = "broadcast.yaml";
  private static final String NODE_ID = "test_node";

  @Mock private BroadcastProperties properties;
  @Mock private DefinitionLoader loader;

  @InjectMocks private InMemoryBroadcastStorage broadcastStorage;

  @Mock private BroadcastMap broadcastMap;
  @Mock private BroadcastNode broadcastNode;

  @BeforeEach
  void setUp() {
    lenient().when(properties.fileName()).thenReturn(FILE_NAME);
  }

  @Test
  void warmUp_shouldLoadBroadcastMapAndLogSuccess_whenCalled() {
    when(loader.load(FILE_NAME, BroadcastMap.class)).thenReturn(broadcastMap);

    try (TestLogCaptor logCaptor = new TestLogCaptor(InMemoryBroadcastStorage.class)) {
      broadcastStorage.warmUp();

      verify(loader).load(FILE_NAME, BroadcastMap.class);

      ILoggingEvent startLog = logCaptor.events().getFirst();
      assertEquals(Level.INFO, startLog.getLevel());
      assertTrue(startLog.getFormattedMessage().contains("Warming up broadcast cache"));
      assertTrue(startLog.getFormattedMessage().contains(FILE_NAME));

      ILoggingEvent successLog = logCaptor.events().get(1);
      assertEquals(Level.INFO, successLog.getLevel());
      assertTrue(successLog.getFormattedMessage().contains("warmed up successfully"));
      assertTrue(successLog.getFormattedMessage().contains(FILE_NAME));
    }
  }

  @Test
  void isEnabled_shouldReturnValueFromBroadcastProperties_whenCalled() {
    when(properties.enabled()).thenReturn(true);
    assertTrue(broadcastStorage.isEnabled());
  }

  @Test
  void isEnabled_shouldReturnFalse_whenBroadcastIsDisabled() {
    when(properties.enabled()).thenReturn(false);
    assertFalse(broadcastStorage.isEnabled());
  }

  @Test
  void getNode_shouldReturnNodeFromHolder_whenWarmedUp() {
    when(loader.load(FILE_NAME, BroadcastMap.class)).thenReturn(broadcastMap);
    when(broadcastMap.getNode(NODE_ID)).thenReturn(broadcastNode);

    broadcastStorage.warmUp();

    BroadcastNode result = broadcastStorage.getNode(NODE_ID);

    assertEquals(broadcastNode, result);
  }

  @Test
  void getMap_shouldReturnNodesMapFromHolder_whenWarmedUp() {
    Map<String, BroadcastNode> expectedMap = Map.of(NODE_ID, broadcastNode);
    when(loader.load(FILE_NAME, BroadcastMap.class)).thenReturn(broadcastMap);
    when(broadcastMap.nodes()).thenReturn(expectedMap);

    broadcastStorage.warmUp();

    Map<String, BroadcastNode> result = broadcastStorage.getMap();

    assertEquals(expectedMap, result);
  }
}
