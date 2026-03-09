package com.github.jawisimo.botengine.interaction.content;

import static org.mockito.Mockito.*;

import com.github.jawisimo.botengine.interaction.content.handler.ContentHandler;
import com.github.jawisimo.botengine.model.Button;
import com.github.jawisimo.botengine.model.ButtonType;
import com.github.jawisimo.botengine.model.ContentNode;
import com.github.jawisimo.botengine.model.DialogNode;
import com.github.jawisimo.botengine.repository.MessageRepository;
import com.github.jawisimo.botengine.validator.DialogValidator;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.message.Message;

@ExtendWith(MockitoExtension.class)
class ContentExecutorTest {

  private static final String CHAT_ID = "123456789";
  private static final Integer MESSAGE_ID = 42;
  private static final String MESSAGE_TEXT = "Test message";
  private static final ButtonType BUTTON_TYPE = ButtonType.INLINE;
  private static final List<Button> BUTTONS = List.of(mock(Button.class));

  @Mock private ContentHandler textHandler;
  @Mock private ContentHandler photoHandler;
  @Mock private ContentHandler audioHandler;
  @Mock private ContentHandler videoHandler;
  @Mock private MessageRepository messageRepository;
  @Mock private DialogValidator dialogValidator;

  private ContentExecutor contentExecutor;

  @BeforeEach
  void setUp() {
    contentExecutor =
        new ContentExecutor(
            List.of(textHandler, photoHandler, audioHandler, videoHandler), messageRepository);
  }

  @Test
  void execute_shouldDoNothing_whenNodeContentIsNull() {
    DialogNode node = new DialogNode(null, MESSAGE_TEXT, BUTTON_TYPE, BUTTONS);

    contentExecutor.execute(node, CHAT_ID);

    verifyNoInteractions(
        textHandler, photoHandler, audioHandler, videoHandler, messageRepository, dialogValidator);
  }

  @Test
  void execute_shouldProcessAllContentNodesWithDifferentHandlers() {
    ContentNode textNode = mock(ContentNode.class);
    ContentNode photoNode = mock(ContentNode.class);
    DialogNode node =
        new DialogNode(List.of(textNode, photoNode), MESSAGE_TEXT, BUTTON_TYPE, BUTTONS);
    Message sentMessage1 = mock(Message.class);
    Message sentMessage2 = mock(Message.class);

    when(textHandler.canHandle(any())).thenReturn(false);
    when(textHandler.canHandle(textNode)).thenReturn(true);
    when(textHandler.handle(textNode, CHAT_ID)).thenReturn(sentMessage1);
    when(sentMessage1.getMessageId()).thenReturn(MESSAGE_ID);
    when(photoHandler.canHandle(any())).thenReturn(false);
    when(photoHandler.canHandle(photoNode)).thenReturn(true);
    when(photoHandler.handle(photoNode, CHAT_ID)).thenReturn(sentMessage2);
    when(sentMessage2.getMessageId()).thenReturn(MESSAGE_ID + 1);
    when(audioHandler.canHandle(any())).thenReturn(false);
    when(videoHandler.canHandle(any())).thenReturn(false);

    contentExecutor.execute(node, CHAT_ID);

    verify(textHandler).canHandle(textNode);
    verify(textHandler).handle(textNode, CHAT_ID);
    verify(messageRepository).save(CHAT_ID, MESSAGE_ID);
    verify(photoHandler).canHandle(photoNode);
    verify(photoHandler).handle(photoNode, CHAT_ID);
    verify(messageRepository).save(CHAT_ID, MESSAGE_ID + 1);
    verify(audioHandler).canHandle(textNode);
    verify(audioHandler).canHandle(photoNode);
    verify(audioHandler, never()).handle(any(), any());
    verify(videoHandler).canHandle(textNode);
    verify(videoHandler).canHandle(photoNode);
    verify(videoHandler, never()).handle(any(), any());
  }

  @Test
  void execute_shouldUseFirstHandlerThatCanHandleForEachContentNode() {
    ContentNode contentNode = mock(ContentNode.class);
    DialogNode node = new DialogNode(List.of(contentNode), MESSAGE_TEXT, BUTTON_TYPE, BUTTONS);
    Message sentMessage = mock(Message.class);

    when(textHandler.canHandle(contentNode)).thenReturn(false);
    when(photoHandler.canHandle(contentNode)).thenReturn(true);
    when(photoHandler.handle(contentNode, CHAT_ID)).thenReturn(sentMessage);
    when(sentMessage.getMessageId()).thenReturn(MESSAGE_ID);
    when(audioHandler.canHandle(contentNode)).thenReturn(false);
    when(videoHandler.canHandle(contentNode)).thenReturn(false);

    contentExecutor.execute(node, CHAT_ID);

    InOrder inOrder =
        inOrder(textHandler, photoHandler, audioHandler, videoHandler, messageRepository);
    inOrder.verify(textHandler).canHandle(contentNode);
    inOrder.verify(photoHandler).canHandle(contentNode);
    inOrder.verify(photoHandler).handle(contentNode, CHAT_ID);
    inOrder.verify(messageRepository).save(CHAT_ID, MESSAGE_ID);
    inOrder.verify(audioHandler).canHandle(contentNode);
    inOrder.verify(videoHandler).canHandle(contentNode);
    verify(audioHandler, never()).handle(any(), any());
    verify(videoHandler, never()).handle(any(), any());
  }

  @Test
  void execute_shouldValidateEachContentNodeBeforeProcessing() {
    ContentNode contentNode1 = mock(ContentNode.class);
    ContentNode contentNode2 = mock(ContentNode.class);
    DialogNode node =
        new DialogNode(List.of(contentNode1, contentNode2), MESSAGE_TEXT, BUTTON_TYPE, BUTTONS);

    when(textHandler.canHandle(any())).thenReturn(true);
    when(textHandler.handle(any(), any())).thenReturn(mock(Message.class));

    contentExecutor.execute(node, CHAT_ID);

    InOrder inOrder = inOrder(dialogValidator, textHandler);
    inOrder.verify(textHandler).canHandle(contentNode1);
    inOrder.verify(textHandler).handle(contentNode1, CHAT_ID);
    inOrder.verify(textHandler).canHandle(contentNode2);
    inOrder.verify(textHandler).handle(contentNode2, CHAT_ID);
  }

  @Test
  void execute_shouldContinueToNextContentNode_whenNoHandlerCanHandleCurrent() {
    ContentNode unsupportedNode = mock(ContentNode.class);
    ContentNode textNode = mock(ContentNode.class);
    DialogNode node =
        new DialogNode(List.of(unsupportedNode, textNode), MESSAGE_TEXT, BUTTON_TYPE, BUTTONS);

    when(textHandler.canHandle(unsupportedNode)).thenReturn(false);
    when(photoHandler.canHandle(unsupportedNode)).thenReturn(false);
    when(audioHandler.canHandle(unsupportedNode)).thenReturn(false);
    when(videoHandler.canHandle(unsupportedNode)).thenReturn(false);
    when(textHandler.canHandle(textNode)).thenReturn(true);
    when(textHandler.handle(textNode, CHAT_ID)).thenReturn(mock(Message.class));

    contentExecutor.execute(node, CHAT_ID);

    verify(textHandler).canHandle(unsupportedNode);
    verify(photoHandler).canHandle(unsupportedNode);
    verify(audioHandler).canHandle(unsupportedNode);
    verify(videoHandler).canHandle(unsupportedNode);
    verify(textHandler).canHandle(textNode);
    verify(textHandler).handle(textNode, CHAT_ID);
    verify(textHandler, never()).handle(unsupportedNode, CHAT_ID);
    verify(photoHandler, never()).handle(any(), any());
    verify(audioHandler, never()).handle(any(), any());
    verify(videoHandler, never()).handle(any(), any());
  }

  @Test
  void execute_shouldNotSaveMessage_whenHandlerReturnsNull() {
    ContentNode contentNode = mock(ContentNode.class);
    DialogNode node = new DialogNode(List.of(contentNode), MESSAGE_TEXT, BUTTON_TYPE, BUTTONS);

    when(textHandler.canHandle(contentNode)).thenReturn(true);
    when(textHandler.handle(contentNode, CHAT_ID)).thenReturn(null);
    when(photoHandler.canHandle(contentNode)).thenReturn(false);
    when(audioHandler.canHandle(contentNode)).thenReturn(false);
    when(videoHandler.canHandle(contentNode)).thenReturn(false);

    contentExecutor.execute(node, CHAT_ID);

    verify(textHandler).handle(contentNode, CHAT_ID);
    verify(messageRepository, never()).save(anyString(), anyInt());
  }

  @Test
  void execute_shouldCallCanHandleOnAllHandlersForEachContentNode() {
    ContentNode contentNode = mock(ContentNode.class);
    DialogNode node = new DialogNode(List.of(contentNode), MESSAGE_TEXT, BUTTON_TYPE, BUTTONS);

    when(textHandler.canHandle(contentNode)).thenReturn(true);
    when(textHandler.handle(contentNode, CHAT_ID)).thenReturn(mock(Message.class));
    when(photoHandler.canHandle(contentNode)).thenReturn(false);
    when(audioHandler.canHandle(contentNode)).thenReturn(false);
    when(videoHandler.canHandle(contentNode)).thenReturn(false);

    contentExecutor.execute(node, CHAT_ID);

    verify(textHandler).canHandle(contentNode);
    verify(photoHandler).canHandle(contentNode);
    verify(audioHandler).canHandle(contentNode);
    verify(videoHandler).canHandle(contentNode);
    verify(textHandler).handle(contentNode, CHAT_ID);
    verify(photoHandler, never()).handle(any(), any());
    verify(audioHandler, never()).handle(any(), any());
    verify(videoHandler, never()).handle(any(), any());
  }
}
