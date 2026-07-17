package io.github.devexhale.botengine.execution.broadcast;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.github.devexhale.botengine.domain.broadcast.BroadcastNode;
import io.github.devexhale.botengine.domain.content.ContentNode;
import io.github.devexhale.botengine.execution.common.content.ContentExecutor;
import io.github.devexhale.botengine.execution.support.ChatLockRegistry;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BroadcastNodeExecutorTest {

  private static final String CHAT_ID = "123456789";

  @Mock private ContentExecutor contentExecutor;
  @Mock private BroadcastKeyboardExecutor keyboardExecutor;
  @Mock private ChatLockRegistry chatLockRegistry;

  @InjectMocks private BroadcastNodeExecutor broadcastNodeExecutor;

  @Mock private BroadcastNode broadcastNode;

  @Test
  void execute_shouldDelegateToContentAndKeyboardExecutors_whenCalled() {
    List<ContentNode> contentList = List.of();

    when(broadcastNode.content()).thenReturn(contentList);
    when(chatLockRegistry.getLock(CHAT_ID)).thenReturn(new ReentrantLock());

    broadcastNodeExecutor.execute(broadcastNode, CHAT_ID);

    verify(contentExecutor).execute(contentList, CHAT_ID);
    verify(keyboardExecutor).execute(broadcastNode, CHAT_ID);
  }
}
