// package io.github.jawisimo.botengine.interaction.dialog.keyboard;
//
// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.mockito.Mockito.*;
//
// import dialog.domain.io.github.devexhale.botengine.Button;
// import dialog.domain.io.github.devexhale.botengine.ButtonType;
// import dialog.domain.io.github.devexhale.botengine.DialogNode;
// import keyboard.dialog.execution.io.github.devexhale.botengine.DialogKeyboardExecutor;
// import keyboard.dialog.execution.io.github.devexhale.botengine.DialogKeyboardMarkupBuilder;
// import io.github.jawisimo.botengine.repository.message.dialog.MessageCleanupRepository;
// import java.util.List;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.ArgumentCaptor;
// import org.mockito.Captor;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;
// import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
// import org.telegram.telegrambots.meta.api.objects.message.Message;
// import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
// import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
// import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
// import org.telegram.telegrambots.meta.generics.TelegramClient;
//
// @ExtendWith(MockitoExtension.class)
// class DialogKeyboardExecutorTest {
//
//  private static final String CHAT_ID = "120L";
//  private static final String NODE_MESSAGE = "hello";
//  private static final Integer MESSAGE_ID = 77;
//
//  private static final String BUTTON_LABEL = "btn";
//  private static final String BUTTON_NEXT = "next";
//  private static final String BUTTON_URL = "https://example.com";
//
//  @Mock private TelegramClient client;
//  @Mock private DialogKeyboardMarkupBuilder keyboardBuilder;
//  @Mock private MessageCleanupRepository messageRepository;
//
//  @InjectMocks private DialogKeyboardExecutor dialogKeyboardExecutor;
//
//  @Mock private Message sentMsg;
//
//  @Captor private ArgumentCaptor<SendMessage> sendMessageCaptor;
//
//  private Button button;
//
//  @BeforeEach
//  void init() {
//    button = new Button(BUTTON_LABEL, BUTTON_NEXT, BUTTON_URL);
//  }
//
//  @Test
//  void execute_shouldSendReplyKeyboardAndSaveMessageId_whenNodeButtonTypeIsReply()
//      throws TelegramApiException {
//    List<Button> buttons = List.of(button);
//    DialogNode node = new DialogNode(null, NODE_MESSAGE, ButtonType.REPLY, buttons);
//
//    ReplyKeyboardMarkup replyMarkup = ReplyKeyboardMarkup.builder().build();
//
//    when(keyboardBuilder.buildReplyKeyboard(buttons)).thenReturn(replyMarkup);
//    when(client.execute(any(SendMessage.class))).thenReturn(sentMsg);
//    when(sentMsg.getMessageId()).thenReturn(MESSAGE_ID);
//
//    dialogKeyboardExecutor.execute(node, CHAT_ID);
//
//    verify(keyboardBuilder).buildReplyKeyboard(buttons);
//    verify(keyboardBuilder, never()).buildInlineKeyboard(anyList());
//    verify(client).execute(sendMessageCaptor.capture());
//    SendMessage actual = sendMessageCaptor.getValue();
//    assertEquals(CHAT_ID, actual.getChatId());
//    assertEquals(NODE_MESSAGE, actual.getText());
//    assertEquals(replyMarkup, actual.getReplyMarkup());
//    verify(messageRepository).save(CHAT_ID, MESSAGE_ID);
//  }
//
//  @Test
//  void execute_shouldSendInlineKeyboardAndSaveMessageId_whenNodeButtonTypeIsInline()
//      throws TelegramApiException {
//    List<Button> buttons = List.of(button);
//    DialogNode node = new DialogNode(null, NODE_MESSAGE, ButtonType.INLINE, buttons);
//
//    InlineKeyboardMarkup inlineMarkup = InlineKeyboardMarkup.builder().build();
//
//    when(keyboardBuilder.buildInlineKeyboard(buttons)).thenReturn(inlineMarkup);
//    when(client.execute(any(SendMessage.class))).thenReturn(sentMsg);
//    when(sentMsg.getMessageId()).thenReturn(MESSAGE_ID);
//
//    dialogKeyboardExecutor.execute(node, CHAT_ID);
//
//    verify(keyboardBuilder).buildInlineKeyboard(buttons);
//    verify(keyboardBuilder, never()).buildReplyKeyboard(anyList());
//    verify(client).execute(sendMessageCaptor.capture());
//    SendMessage actual = sendMessageCaptor.getValue();
//    assertEquals(CHAT_ID, actual.getChatId());
//    assertEquals(NODE_MESSAGE, actual.getText());
//    assertEquals(inlineMarkup, actual.getReplyMarkup());
//    verify(messageRepository).save(CHAT_ID, MESSAGE_ID);
//  }
//
//  @Test
//  void execute_shouldNotSaveMessageId_whenTelegramClientThrowsTelegramApiException()
//      throws TelegramApiException {
//    List<Button> buttons = List.of(button);
//    DialogNode node = new DialogNode(null, NODE_MESSAGE, ButtonType.INLINE, buttons);
//
//    InlineKeyboardMarkup inlineMarkup = InlineKeyboardMarkup.builder().build();
//    TelegramApiException exception = new TelegramApiException("Some Telegram API error...");
//
//    when(keyboardBuilder.buildInlineKeyboard(buttons)).thenReturn(inlineMarkup);
//    when(client.execute(any(SendMessage.class))).thenThrow(exception);
//
//    dialogKeyboardExecutor.execute(node, CHAT_ID);
//
//    verify(keyboardBuilder).buildInlineKeyboard(buttons);
//    verify(client).execute(any(SendMessage.class));
//    verify(messageRepository, never()).save(anyString(), anyInt());
//  }
// }
