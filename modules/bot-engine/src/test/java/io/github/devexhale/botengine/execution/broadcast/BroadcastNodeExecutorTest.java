package io.github.devexhale.botengine.execution.broadcast;

import io.github.devexhale.botengine.domain.broadcast.BroadcastNode;
import io.github.devexhale.botengine.domain.content.ContentNode;
import io.github.devexhale.botengine.execution.common.content.ContentExecutor;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BroadcastNodeExecutorTest {

  private static final String CHAT_ID = "123456789";

  @Mock private ContentExecutor contentExecutor;
  @Mock private BroadcastKeyboardExecutor keyboardExecutor;

  @InjectMocks private BroadcastNodeExecutor broadcastNodeExecutor;

  @Mock private BroadcastNode broadcastNode;

  @Test
  void execute_shouldDelegateToContentAndKeyboardExecutors_whenCalled() {
    List<ContentNode> contentList = List.of();

    when(broadcastNode.content()).thenReturn(contentList);

    broadcastNodeExecutor.execute(broadcastNode, CHAT_ID);

    verify(contentExecutor).execute(contentList, CHAT_ID);
    verify(keyboardExecutor).execute(broadcastNode, CHAT_ID);
  }
}
