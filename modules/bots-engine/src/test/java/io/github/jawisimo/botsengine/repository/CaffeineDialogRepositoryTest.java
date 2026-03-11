package io.github.jawisimo.botsengine.repository;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

import io.github.jawisimo.botsengine.config.bot.BotProperties;
import io.github.jawisimo.botsengine.model.DialogMap;
import io.github.jawisimo.botsengine.model.DialogNode;
import io.github.jawisimo.botsengine.loader.DialogLoader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CaffeineDialogRepositoryTest {

  private static final String DIALOG_FILE_YML = "dialog.yml";
  private static final String DIALOG_FILE_JSON = "dialog.json";

  @Mock private BotProperties properties;
  @Mock private DialogLoader dialogLoader;
  @InjectMocks private CaffeineDialogRepository repository;

  @Mock private DialogMap dialogMap;

  @Test
  void shouldLoadDialogOnlyOnce_whenSameDialogFileNameRequestedMultipleTimes() {
    String firstNodeId = "node-1";
    String secondNodeId = "node-2";
    int expectedLoadCount = 1;

    DialogNode firstNode = mock(DialogNode.class);
    DialogNode secondNode = mock(DialogNode.class);

    when(properties.dialogFileName()).thenReturn(DIALOG_FILE_YML);
    when(dialogLoader.load(DIALOG_FILE_YML)).thenReturn(dialogMap);
    when(dialogMap.getNode(firstNodeId)).thenReturn(firstNode);
    when(dialogMap.getNode(secondNodeId)).thenReturn(secondNode);

    DialogNode firstResult = repository.getNode(firstNodeId);
    DialogNode secondResult = repository.getNode(secondNodeId);

    assertSame(firstNode, firstResult);
    assertSame(secondNode, secondResult);
    verify(dialogLoader, times(expectedLoadCount)).load(DIALOG_FILE_YML);
    verify(dialogMap).getNode(firstNodeId);
    verify(dialogMap).getNode(secondNodeId);
    verifyNoMoreInteractions(dialogLoader);
  }

  @Test
  void shouldPassDialogFileNameToLoader_whenCacheMissOccurs() {
    String nodeId = "node-87";
    DialogNode node = mock(DialogNode.class);

    when(properties.dialogFileName()).thenReturn(DIALOG_FILE_JSON);
    when(dialogLoader.load(DIALOG_FILE_JSON)).thenReturn(dialogMap);
    when(dialogMap.getNode(nodeId)).thenReturn(node);

    DialogNode result = repository.getNode(nodeId);

    assertSame(node, result);
    verify(dialogLoader).load(DIALOG_FILE_JSON);
    verify(dialogLoader, never()).load(argThat(arg -> !DIALOG_FILE_JSON.equals(arg)));
  }

  @Test
  void shouldWarmUpCacheOnInitialization() {
    when(properties.dialogFileName()).thenReturn(DIALOG_FILE_YML);
    when(dialogLoader.load(DIALOG_FILE_YML)).thenReturn(dialogMap);

    repository.afterPropertiesSet();

    verify(dialogLoader).load(DIALOG_FILE_YML);
  }
}
