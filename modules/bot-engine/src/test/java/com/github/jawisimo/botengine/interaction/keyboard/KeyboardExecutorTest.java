package com.github.jawisimo.botengine.interaction.keyboard;

import com.github.jawisimo.botengine.interaction.node.model.Button;
import com.github.jawisimo.botengine.interaction.node.model.ButtonType;
import com.github.jawisimo.botengine.interaction.node.model.DialogNode;
import com.github.jawisimo.botengine.repository.MessageRepository;
import com.github.jawisimo.botengine.validator.DialogValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KeyboardExecutorTest {

  private static final String CHAT_ID = "123";
  private static final String NODE_MESSAGE = "hello";
  private static final Integer MESSAGE_ID = 77;

  private static final String BUTTON_LABEL = "btn";
  private static final String BUTTON_NEXT = "next";
  private static final String BUTTON_URL = "https://example.com";

  @Mock private TelegramClient client;
  @Mock private KeyboardMarkupBuilder keyboardBuilder;
  @Mock private DialogValidator dialogValidator;
  @Mock private MessageRepository messageRepository;

  @InjectMocks private KeyboardExecutor keyboardExecutor;

  @Captor private ArgumentCaptor<SendMessage> sendMessageCaptor;

  @Test
  void execute_shouldSendReplyKeyboardAndSaveMessageId_whenNodeButtonTypeIsReply()
      throws TelegramApiException {

    Button button = new Button(BUTTON_LABEL, BUTTON_NEXT, BUTTON_URL);
    List<Button> buttons = List.of(button);
    DialogNode node = new DialogNode(null, NODE_MESSAGE, ButtonType.REPLY, buttons);

    ReplyKeyboardMarkup replyMarkup = ReplyKeyboardMarkup.builder().build();
    Message sentMessage = mock(Message.class);

    when(keyboardBuilder.buildReplyKeyboard(buttons)).thenReturn(replyMarkup);
    when(client.execute(any(SendMessage.class))).thenReturn(sentMessage);
    when(sentMessage.getMessageId()).thenReturn(MESSAGE_ID);

    keyboardExecutor.execute(node, CHAT_ID);

    verify(dialogValidator).validateButtons(node);
    verify(keyboardBuilder).buildReplyKeyboard(buttons);
    verify(keyboardBuilder, never()).buildInlineKeyboard(anyList());
    verify(client).execute(sendMessageCaptor.capture());
    SendMessage actual = sendMessageCaptor.getValue();
    assertEquals(CHAT_ID, actual.getChatId());
    assertEquals(NODE_MESSAGE, actual.getText());
    assertEquals(replyMarkup, actual.getReplyMarkup());
    verify(messageRepository).save(CHAT_ID, MESSAGE_ID);
  }

  @Test
  void execute_shouldSendInlineKeyboardAndSaveMessageId_whenNodeButtonTypeIsInline()
      throws TelegramApiException {

    Button button = new Button(BUTTON_LABEL, BUTTON_NEXT, BUTTON_URL);
    List<Button> buttons = List.of(button);
    DialogNode node = new DialogNode(null, NODE_MESSAGE, ButtonType.INLINE, buttons);

    InlineKeyboardMarkup inlineMarkup = InlineKeyboardMarkup.builder().build();
    Message sentMessage = mock(Message.class);

    when(keyboardBuilder.buildInlineKeyboard(buttons)).thenReturn(inlineMarkup);
    when(client.execute(any(SendMessage.class))).thenReturn(sentMessage);
    when(sentMessage.getMessageId()).thenReturn(MESSAGE_ID);

    keyboardExecutor.execute(node, CHAT_ID);

    verify(dialogValidator).validateButtons(node);
    verify(keyboardBuilder).buildInlineKeyboard(buttons);
    verify(keyboardBuilder, never()).buildReplyKeyboard(anyList());
    verify(client).execute(sendMessageCaptor.capture());
    SendMessage actual = sendMessageCaptor.getValue();
    assertEquals(CHAT_ID, actual.getChatId());
    assertEquals(NODE_MESSAGE, actual.getText());
    assertEquals(inlineMarkup, actual.getReplyMarkup());
    verify(messageRepository).save(CHAT_ID, MESSAGE_ID);
  }

  @Test
  void execute_shouldNotSaveMessageId_whenTelegramClientThrowsTelegramApiException()
      throws TelegramApiException {

    Button button = new Button(BUTTON_LABEL, BUTTON_NEXT, BUTTON_URL);
    List<Button> buttons = List.of(button);
    DialogNode node = new DialogNode(null, NODE_MESSAGE, ButtonType.INLINE, buttons);

    InlineKeyboardMarkup inlineMarkup = InlineKeyboardMarkup.builder().build();
    TelegramApiException exception = new TelegramApiException("Some Telegram API error...");

    when(keyboardBuilder.buildInlineKeyboard(buttons)).thenReturn(inlineMarkup);
    when(client.execute(any(SendMessage.class))).thenThrow(exception);

    keyboardExecutor.execute(node, CHAT_ID);

    verify(dialogValidator).validateButtons(node);
    verify(keyboardBuilder).buildInlineKeyboard(buttons);
    verify(client).execute(any(SendMessage.class));
    verify(messageRepository, never()).save(anyString(), anyInt());
  }
}
