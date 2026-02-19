package repository;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

import com.github.jawisimo.tbcfstarter.config.BotProperties;
import com.github.jawisimo.tbcfstarter.interaction.node.model.DialogMap;
import com.github.jawisimo.tbcfstarter.interaction.node.model.DialogNode;
import com.github.jawisimo.tbcfstarter.loader.DialogLoader;
import com.github.jawisimo.tbcfstarter.repository.CaffeineDialogRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CaffeineDialogRepositoryTest {

  @Test
  void shouldLoadDialogOnlyOnce_whenSameDialogFileNameRequestedMultipleTimes() {
    BotProperties properties = mock(BotProperties.class);
    DialogLoader dialogLoader = mock(DialogLoader.class);
    DialogMap dialogMap = mock(DialogMap.class);

    String dialogFileName = "dialog.yml";
    String firstNodeId = "node-1";
    String secondNodeId = "node-2";
    int expectedLoadCount = 1;

    DialogNode firstNode = mock(DialogNode.class);
    DialogNode secondNode = mock(DialogNode.class);

    when(properties.dialogFileName()).thenReturn(dialogFileName);
    when(dialogLoader.load(dialogFileName)).thenReturn(dialogMap);
    when(dialogMap.getNode(firstNodeId)).thenReturn(firstNode);
    when(dialogMap.getNode(secondNodeId)).thenReturn(secondNode);

    CaffeineDialogRepository repository = new CaffeineDialogRepository(properties, dialogLoader);

    DialogNode firstResult = repository.getNode(firstNodeId);
    DialogNode secondResult = repository.getNode(secondNodeId);

    assertSame(firstNode, firstResult);
    assertSame(secondNode, secondResult);
    verify(dialogLoader, times(expectedLoadCount)).load(dialogFileName);
    verify(dialogMap).getNode(firstNodeId);
    verify(dialogMap).getNode(secondNodeId);
    verifyNoMoreInteractions(dialogLoader);
  }

  @Test
  void shouldPassDialogFileNameToLoader_whenCacheMissOccurs() {
    BotProperties properties = mock(BotProperties.class);
    DialogLoader dialogLoader = mock(DialogLoader.class);
    DialogMap dialogMap = mock(DialogMap.class);

    String dialogFileName = "dialog.json";
    String nodeId = "node-1";
    DialogNode node = mock(DialogNode.class);

    when(properties.dialogFileName()).thenReturn(dialogFileName);
    when(dialogLoader.load(dialogFileName)).thenReturn(dialogMap);
    when(dialogMap.getNode(nodeId)).thenReturn(node);

    CaffeineDialogRepository repository = new CaffeineDialogRepository(properties, dialogLoader);
    DialogNode result = repository.getNode(nodeId);

    assertSame(node, result);
    verify(dialogLoader).load(dialogFileName);
    verify(dialogLoader, never()).load(argThat(arg -> !dialogFileName.equals(arg)));
  }
}
