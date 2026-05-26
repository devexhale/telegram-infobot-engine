// package io.github.jawisimo.botengine.interaction.dialog.content.handler;
//
// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.Mockito.*;
//
// import content.domain.io.github.devexhale.botengine.ContentNode;
// import handler.content.common.execution.io.github.devexhale.botengine.VideoMediaHandler;
// import loader.io.github.devexhale.botengine.MediaFileLoader;
// import org.junit.jupiter.api.Test;
// import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
// import org.telegram.telegrambots.meta.api.objects.message.Message;
// import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
// import org.telegram.telegrambots.meta.generics.TelegramClient;
//
// class VideoMediaHandlerTest extends BaseMediaHandlerTest<VideoMediaHandler> {
//
//  private static final String MEDIA_TYPE = "VIDEO";
//
//  @Override
//  protected VideoMediaHandler createHandler(
//      TelegramClient client, MediaFileLoader mediaFileLoader) {
//    return new VideoMediaHandler(client, mediaFileLoader);
//  }
//
//  @Override
//  protected String getExpectedMediaType() {
//    return MEDIA_TYPE;
//  }
//
//  @Test
//  void handle_shouldSendVideoSuccessfully() throws TelegramApiException {
//    ContentNode contentNode = createContentNodeWithExpectedMediaType(CAPTION);
//
//    when(telegramClient.execute(any(SendVideo.class))).thenReturn(expectedMsg);
//
//    Message result = handler.handle(contentNode, CHAT_ID);
//
//    assertNotNull(result);
//    assertEquals(expectedMsg, result);
//    verify(mediaFileLoader).load(FILE_NAME);
//    verify(telegramClient).execute(any(SendVideo.class));
//  }
//
//  @Test
//  void handle_shouldReturnNull_whenTelegramApiExceptionOccurs() throws TelegramApiException {
//    ContentNode contentNode = createContentNodeWithExpectedMediaType(CAPTION);
//
//    when(telegramClient.execute(any(SendVideo.class)))
//        .thenThrow(new TelegramApiException(EXCEPTION_MSG));
//
//    Message result = handler.handle(contentNode, CHAT_ID);
//
//    assertNull(result);
//  }
//
//  @Test
//  void handle_shouldHandleSuccessfully_whenCaptionIsNull() throws TelegramApiException {
//    ContentNode contentNode = createContentNodeWithExpectedMediaType(null);
//
//    when(telegramClient.execute(any(SendVideo.class))).thenReturn(expectedMsg);
//
//    Message result = handler.handle(contentNode, CHAT_ID);
//
//    assertNotNull(result);
//    assertEquals(expectedMsg, result);
//  }
//
//  @Test
//  void getMediaType_shouldReturnCorrectType() {
//    assertEquals(MEDIA_TYPE, handler.getMediaType());
//  }
// }
