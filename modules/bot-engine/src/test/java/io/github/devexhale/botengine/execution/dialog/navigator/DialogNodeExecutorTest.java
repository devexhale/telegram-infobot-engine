package io.github.devexhale.botengine.execution.dialog.navigator;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.github.devexhale.botengine.domain.content.ContentNode;
import io.github.devexhale.botengine.domain.dialog.Button;
import io.github.devexhale.botengine.domain.dialog.ButtonType;
import io.github.devexhale.botengine.domain.dialog.DialogNode;
import io.github.devexhale.botengine.execution.common.content.ContentExecutor;
import io.github.devexhale.botengine.execution.dialog.keyboard.DialogKeyboardExecutor;
import io.github.devexhale.botengine.execution.support.MessageCleanupManager;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.message.Message;

@ExtendWith(MockitoExtension.class)
class DialogNodeExecutorTest {

  private static final String CHAT_ID = "123456789";
  private static final String NODE_MESSAGE = "Hello from node!";

  @Mock private ContentExecutor contentExecutor;
  @Mock private DialogKeyboardExecutor dialogKeyboardExecutor;
  @Mock private MessageCleanupManager messageCleanupManager;

  @InjectMocks private DialogNodeExecutor dialogNodeExecutor;

  @Mock private Message contentMessage1;
  @Mock private Message contentMessage2;
  @Mock private Message keyboardMessage;

  @Captor private ArgumentCaptor<List<Message>> messageListCaptor;

  private DialogNode node;
  private List<ContentNode> contentNodes;

  @BeforeEach
  void setUp() {
    Button button = new Button("Click me", "callback_data", "https://example.com");
    contentNodes = List.of();
    node = new DialogNode(contentNodes, NODE_MESSAGE, ButtonType.INLINE, List.of(button));
  }

  @Test
  void execute_shouldRegistersAllMessages_whenContentAndKeyboardArePresent() {
    List<Message> contentMessages = List.of(contentMessage1, contentMessage2);
    List<Message> keyboardMessages = List.of(keyboardMessage);

    when(contentExecutor.execute(contentNodes, CHAT_ID)).thenReturn(contentMessages);
    when(dialogKeyboardExecutor.execute(node, CHAT_ID)).thenReturn(keyboardMessages);

    dialogNodeExecutor.execute(node, CHAT_ID);

    verify(messageCleanupManager).cleanLastNode(CHAT_ID);
    verify(messageCleanupManager)
        .registerMessagesForCleanup(eq(CHAT_ID), messageListCaptor.capture());
    assertIterableEquals(
        List.of(contentMessage1, contentMessage2, keyboardMessage), messageListCaptor.getValue());
  }

  @Test
  void execute_shouldRegistersMessages_whenContentIsEmpty() {
    when(contentExecutor.execute(contentNodes, CHAT_ID)).thenReturn(List.of());
    when(dialogKeyboardExecutor.execute(node, CHAT_ID)).thenReturn(List.of(keyboardMessage));

    dialogNodeExecutor.execute(node, CHAT_ID);

    verify(messageCleanupManager)
        .registerMessagesForCleanup(eq(CHAT_ID), messageListCaptor.capture());
    assertIterableEquals(List.of(keyboardMessage), messageListCaptor.getValue());
  }

  @Test
  void execute_shouldRegistersMessages_whenKeyboardIsEmpty() {
    List<Message> contentMessages = List.of(contentMessage1, contentMessage2);

    when(contentExecutor.execute(contentNodes, CHAT_ID)).thenReturn(contentMessages);
    when(dialogKeyboardExecutor.execute(node, CHAT_ID)).thenReturn(List.of());

    dialogNodeExecutor.execute(node, CHAT_ID);

    verify(messageCleanupManager)
        .registerMessagesForCleanup(eq(CHAT_ID), messageListCaptor.capture());
    assertIterableEquals(contentMessages, messageListCaptor.getValue());
  }

  @Test
  void execute_shouldDoesNotRegisterCleanup_whenNoMessagesSent() {
    when(contentExecutor.execute(contentNodes, CHAT_ID)).thenReturn(List.of());
    when(dialogKeyboardExecutor.execute(node, CHAT_ID)).thenReturn(List.of());

    dialogNodeExecutor.execute(node, CHAT_ID);

    verify(messageCleanupManager).cleanLastNode(CHAT_ID);
    verify(messageCleanupManager, never()).registerMessagesForCleanup(any(), any());
  }
}
