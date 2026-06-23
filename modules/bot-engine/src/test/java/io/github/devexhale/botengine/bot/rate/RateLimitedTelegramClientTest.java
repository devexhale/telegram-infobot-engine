package io.github.devexhale.botengine.bot.rate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import io.github.devexhale.botengine.testsupport.logger.TestLogCaptor;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.CopyMessage;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.SetMyProfilePhoto;
import org.telegram.telegrambots.meta.api.methods.StopMessageLiveLocation;
import org.telegram.telegrambots.meta.api.methods.business.SetBusinessAccountProfilePhoto;
import org.telegram.telegrambots.meta.api.methods.forum.*;
import org.telegram.telegrambots.meta.api.methods.groupadministration.*;
import org.telegram.telegrambots.meta.api.methods.invoices.SendInvoice;
import org.telegram.telegrambots.meta.api.methods.pinnedmessages.PinChatMessage;
import org.telegram.telegrambots.meta.api.methods.pinnedmessages.UnpinAllChatMessages;
import org.telegram.telegrambots.meta.api.methods.pinnedmessages.UnpinChatMessage;
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll;
import org.telegram.telegrambots.meta.api.methods.polls.StopPoll;
import org.telegram.telegrambots.meta.api.methods.reactions.SetMessageReaction;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.methods.stickers.*;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.*;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.ResponseParameters;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.photo.input.InputProfilePhotoStatic;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@ExtendWith(MockitoExtension.class)
class RateLimitedTelegramClientTest {

  private static final String CHAT_ID = "123456789";
  private static final String MESSAGE_TEXT = "Hello world";
  private static final int MAX_RETRIES = 3;
  private static final int RATE_LIMIT_CODE = 429;
  private static final int RETRY_AFTER_SECONDS = 5;
  private static final int NO_RETRY_DELAY = 0;
  private static final long TEST_USER_ID = 1L;
  private static final int TEST_MESSAGE_ID = 1;
  private static final int TEST_STAR_COUNT = 1;
  private static final String TEST_STICKER_NAME = "name";
  private static final String TEST_STICKER_FORMAT = "static";
  private static final String TEST_URL = "https://test.com";
  private static final String BIZ_CONN_ID = "id";

  private static final String RESOLVE_CHAT_ID_METHOD = "resolveChatId";
  private static final String EXTRACT_RETRY_AFTER_METHOD = "extractRetryAfter";
  private static final String WAIT_FOR_METHOD = "waitFor";

  @Mock private RateLimiter rateLimiter;
  @Mock private TelegramClient delegate;
  @Mock private ScheduledExecutorService scheduledExecutorService;

  @InjectMocks private RateLimitedTelegramClient rateLimitedClient;

  @Mock private Message message;

  @Mock private File ioFile;

  @Mock private InputStream inputStream;

  @Mock private org.telegram.telegrambots.meta.api.objects.File telegramApiFile;

  private SendMessage sendMessage;
  private SendPhoto sendPhoto;

  @BeforeEach
  void setUp() {
    sendMessage = new SendMessage(CHAT_ID, MESSAGE_TEXT);
    sendPhoto = SendPhoto.builder().chatId(CHAT_ID).photo(new InputFile()).build();
  }

  @Test
  void execute_shouldAcquireRateLimitAndDelegate_whenCalledWithSendMessage()
      throws TelegramApiException {
    Message mockMessage = mock(Message.class);
    when(delegate.execute(sendMessage)).thenReturn(mockMessage);

    Message result = rateLimitedClient.execute(sendMessage);

    verify(rateLimiter).acquire(CHAT_ID);
    verify(delegate).execute(sendMessage);
    assertEquals(mockMessage, result);
  }

  @Test
  void execute_shouldAcquireRateLimitWithNullChatId_whenMethodHasNoChatId()
      throws TelegramApiException {
    SetWebhook setWebhook = mock(SetWebhook.class);

    when(delegate.execute(setWebhook)).thenReturn(true);

    Boolean result = rateLimitedClient.execute(setWebhook);

    verify(rateLimiter).acquire(null);
    verify(delegate).execute(setWebhook);
    assertEquals(true, result);
  }

  @Test
  void execute_shouldRetryOn429Error_whenSyncExecutionFailsWithRateLimit()
      throws TelegramApiException {
    int expectedBucketCount = 2;
    TelegramApiRequestException rateLimitException = mock(TelegramApiRequestException.class);
    ResponseParameters responseParameters = mock(ResponseParameters.class);

    when(rateLimitException.getErrorCode()).thenReturn(RATE_LIMIT_CODE);
    when(rateLimitException.getParameters()).thenReturn(responseParameters);
    when(responseParameters.getRetryAfter()).thenReturn(RETRY_AFTER_SECONDS);

    Message mockMessage = mock(Message.class);

    when(delegate.execute(sendMessage)).thenThrow(rateLimitException).thenReturn(mockMessage);

    try (TestLogCaptor logCaptor = new TestLogCaptor(RateLimitedTelegramClient.class)) {
      Message result = rateLimitedClient.execute(sendMessage);

      verify(rateLimiter).acquire(CHAT_ID);
      verify(delegate, times(expectedBucketCount)).execute(sendMessage);
      assertEquals(mockMessage, result);

      ILoggingEvent loggedEvent =
          logCaptor.events().stream()
              .filter(e -> e.getFormattedMessage().contains("Rate limited sync"))
              .findFirst()
              .orElseThrow();
      assertEquals(Level.WARN, loggedEvent.getLevel());
    }
  }

  @Test
  void execute_shouldPropagateError_whenMaxRetriesExceeded() throws TelegramApiException {
    TelegramApiRequestException rateLimitException = mock(TelegramApiRequestException.class);
    ResponseParameters responseParameters = mock(ResponseParameters.class);

    when(rateLimitException.getErrorCode()).thenReturn(RATE_LIMIT_CODE);
    when(rateLimitException.getParameters()).thenReturn(responseParameters);
    when(responseParameters.getRetryAfter()).thenReturn(RETRY_AFTER_SECONDS);

    when(delegate.execute(sendMessage)).thenThrow(rateLimitException);

    assertThrows(TelegramApiException.class, () -> rateLimitedClient.execute(sendMessage));

    verify(delegate, times(MAX_RETRIES)).execute(sendMessage);
  }

  @Test
  void executeAsync_shouldAcquireRateLimitAndDelegate_whenCalled() throws TelegramApiException {
    Message mockMessage = mock(Message.class);
    CompletableFuture<Message> expectedFuture = CompletableFuture.completedFuture(mockMessage);
    when(delegate.executeAsync(sendMessage)).thenReturn(expectedFuture);

    CompletableFuture<Message> result = rateLimitedClient.executeAsync(sendMessage);

    verify(rateLimiter).acquire(CHAT_ID);
    verify(delegate).executeAsync(sendMessage);
    assertEquals(mockMessage, result.join());
  }

  @Test
  void executeAsync_shouldScheduleRetryOn429Error_whenAsyncExecutionFails()
      throws TelegramApiException {
    long expectedDelaySeconds = RETRY_AFTER_SECONDS;
    int expectedScheduleInvocations = 1;

    TelegramApiRequestException rateLimitException = mock(TelegramApiRequestException.class);
    ResponseParameters responseParameters = mock(ResponseParameters.class);

    when(rateLimitException.getErrorCode()).thenReturn(RATE_LIMIT_CODE);
    when(rateLimitException.getParameters()).thenReturn(responseParameters);
    when(responseParameters.getRetryAfter()).thenReturn(RETRY_AFTER_SECONDS);

    CompletableFuture<Message> failedFuture = CompletableFuture.failedFuture(rateLimitException);

    when(delegate.executeAsync(sendMessage)).thenReturn(failedFuture);

    try (TestLogCaptor logCaptor = new TestLogCaptor(RateLimitedTelegramClient.class)) {
      rateLimitedClient.executeAsync(sendMessage);

      verify(rateLimiter).acquire(CHAT_ID);
      verify(delegate).executeAsync(sendMessage);

      verify(scheduledExecutorService, times(expectedScheduleInvocations))
          .schedule(
              ArgumentMatchers.<Callable<?>>any(), eq(expectedDelaySeconds), eq(TimeUnit.SECONDS));

      boolean logFound =
          logCaptor.events().stream()
              .anyMatch(e -> e.getFormattedMessage().contains("Rate limited async"));

      assertTrue(logFound);
    }
  }

  @Test
  void executeAsync_shouldPropagateError_whenMaxRetriesExceeded() throws TelegramApiException {
    TelegramApiRequestException rateLimitException = mock(TelegramApiRequestException.class);
    ResponseParameters responseParameters = mock(ResponseParameters.class);

    when(rateLimitException.getErrorCode()).thenReturn(RATE_LIMIT_CODE);
    when(rateLimitException.getParameters()).thenReturn(responseParameters);
    when(responseParameters.getRetryAfter()).thenReturn(RETRY_AFTER_SECONDS);

    CompletableFuture<Message> failedFuture = CompletableFuture.failedFuture(rateLimitException);

    when(delegate.executeAsync(sendMessage)).thenReturn(failedFuture);

    doAnswer(
            invocation -> {
              Callable<?> retryTask = invocation.getArgument(0);
              retryTask.call();
              return mock(java.util.concurrent.ScheduledFuture.class);
            })
        .when(scheduledExecutorService)
        .schedule(ArgumentMatchers.<Callable<?>>any(), anyLong(), any(TimeUnit.class));

    CompletableFuture<Message> result = rateLimitedClient.executeAsync(sendMessage);

    assertThrows(Exception.class, result::join);

    verify(delegate, times(MAX_RETRIES)).executeAsync(sendMessage);
  }

  @Test
  void executeAsync_shouldNotRetry_whenNonRateLimitError() throws Exception {
    int expectedExecutionCount = 1;
    TelegramApiException nonRateLimitException = mock(TelegramApiException.class);

    CompletableFuture<Message> failedFuture = CompletableFuture.failedFuture(nonRateLimitException);

    when(delegate.executeAsync(sendMessage)).thenReturn(failedFuture);

    CompletableFuture<Message> result = rateLimitedClient.executeAsync(sendMessage);

    assertThrows(Exception.class, result::join);

    verify(delegate, times(expectedExecutionCount)).executeAsync(sendMessage);
    verify(scheduledExecutorService, never())
        .schedule(ArgumentMatchers.<Callable<?>>any(), anyLong(), any(TimeUnit.class));
  }

  @Test
  void executeAsync_shouldPropagateResult_whenRetrySucceeds() throws Exception {
    int expectedAttemptsCount = 2;
    TelegramApiRequestException rateLimitException = mock(TelegramApiRequestException.class);
    ResponseParameters responseParameters = mock(ResponseParameters.class);

    when(rateLimitException.getErrorCode()).thenReturn(RATE_LIMIT_CODE);
    when(rateLimitException.getParameters()).thenReturn(responseParameters);
    when(responseParameters.getRetryAfter()).thenReturn(RETRY_AFTER_SECONDS);

    Message mockMessage = mock(Message.class);

    CompletableFuture<Message> failedFuture = CompletableFuture.failedFuture(rateLimitException);

    when(delegate.executeAsync(sendMessage))
        .thenReturn(failedFuture)
        .thenReturn(CompletableFuture.completedFuture(mockMessage));

    doAnswer(
            invocation -> {
              Callable<?> retryTask = invocation.getArgument(0);
              retryTask.call();
              return mock(java.util.concurrent.ScheduledFuture.class);
            })
        .when(scheduledExecutorService)
        .schedule(ArgumentMatchers.<Callable<?>>any(), anyLong(), any(TimeUnit.class));

    CompletableFuture<Message> result = rateLimitedClient.executeAsync(sendMessage);

    assertEquals(mockMessage, result.join());

    verify(delegate, times(expectedAttemptsCount)).executeAsync(sendMessage);
  }

  @Test
  void executeAsync_shouldSucceedOnFirstAttempt() throws Exception {
    int expectedAttemptsCount = 1;
    Message mockMessage = mock(Message.class);

    when(delegate.executeAsync(sendMessage))
        .thenReturn(CompletableFuture.completedFuture(mockMessage));

    CompletableFuture<Message> result = rateLimitedClient.executeAsync(sendMessage);

    assertEquals(mockMessage, result.join());

    verify(delegate, times(expectedAttemptsCount)).executeAsync(sendMessage);
    verify(scheduledExecutorService, never())
        .schedule(ArgumentMatchers.<Callable<?>>any(), anyLong(), any(TimeUnit.class));
  }

  @Test
  void executeAsync_shouldCatchExceptionAndReturnFailedFuture() throws TelegramApiException {
    RuntimeException ex = new RuntimeException("Test runtime exception");

    when(delegate.executeAsync(sendMessage)).thenThrow(ex);

    CompletableFuture<Message> result = rateLimitedClient.executeAsync(sendMessage);

    assertTrue(result.isCompletedExceptionally());
  }

  @Test
  void executeAsync_shouldCatchTelegramApiExceptionAndReturnFailedFuture()
      throws TelegramApiException {
    TelegramApiException ex = new TelegramApiException("Generic API exception");
    when(delegate.executeAsync(sendMessage)).thenThrow(ex);

    CompletableFuture<Message> result = rateLimitedClient.executeAsync(sendMessage);

    assertTrue(result.isCompletedExceptionally());
  }

  @Test
  void resolveChatId_shouldExtractChatId_fromSendMessage() throws Exception {
    Method method =
        rateLimitedClient.getClass().getDeclaredMethod(RESOLVE_CHAT_ID_METHOD, Object.class);
    method.setAccessible(true);

    String result = (String) method.invoke(rateLimitedClient, sendMessage);

    assertEquals(CHAT_ID, result);
  }

  @Test
  void resolveChatId_shouldExtractChatId_fromSendPhoto() throws Exception {
    Method method =
        rateLimitedClient.getClass().getDeclaredMethod(RESOLVE_CHAT_ID_METHOD, Object.class);
    method.setAccessible(true);

    String result = (String) method.invoke(rateLimitedClient, sendPhoto);

    assertEquals(CHAT_ID, result);
  }

  @Test
  void resolveChatId_shouldReturnNull_forMethodWithoutChatId() throws Exception {
    SetWebhook setWebhook = mock(SetWebhook.class);

    Method method =
        rateLimitedClient.getClass().getDeclaredMethod(RESOLVE_CHAT_ID_METHOD, Object.class);
    method.setAccessible(true);

    String result = (String) method.invoke(rateLimitedClient, setWebhook);

    assertNull(result);
  }

  @Test
  void resolveChatId_coverage() throws Exception {
    Method method =
        rateLimitedClient.getClass().getDeclaredMethod(RESOLVE_CHAT_ID_METHOD, Object.class);
    method.setAccessible(true);

    Object[] methods = getChatIdMethodsMocks();

    for (Object m : methods) {
      Object result = method.invoke(rateLimitedClient, m);
      assertNull(result, "Expected null for unconfigured mock of: " + m.getClass().getSimpleName());
    }
  }

  @Test
  void extractRetryAfter_shouldReturnRetrySeconds_fromTelegramApiRequestException()
      throws Exception {
    TelegramApiRequestException apiException = mock(TelegramApiRequestException.class);
    ResponseParameters params = mock(ResponseParameters.class);

    when(apiException.getErrorCode()).thenReturn(RATE_LIMIT_CODE);
    when(apiException.getParameters()).thenReturn(params);
    when(params.getRetryAfter()).thenReturn(RETRY_AFTER_SECONDS);

    Method method =
        rateLimitedClient.getClass().getDeclaredMethod(EXTRACT_RETRY_AFTER_METHOD, Throwable.class);
    method.setAccessible(true);

    Integer result = (Integer) method.invoke(rateLimitedClient, apiException);

    assertEquals(RETRY_AFTER_SECONDS, result);
  }

  @Test
  void extractRetryAfter_shouldReturnDefaultRetry_whenRetryAfterIsNull() throws Exception {
    TelegramApiRequestException apiException = mock(TelegramApiRequestException.class);
    ResponseParameters params = mock(ResponseParameters.class);

    when(apiException.getErrorCode()).thenReturn(RATE_LIMIT_CODE);
    when(apiException.getParameters()).thenReturn(params);
    when(params.getRetryAfter()).thenReturn(null);

    Method method =
        rateLimitedClient.getClass().getDeclaredMethod(EXTRACT_RETRY_AFTER_METHOD, Throwable.class);
    method.setAccessible(true);

    Integer result = (Integer) method.invoke(rateLimitedClient, apiException);

    assertEquals(RETRY_AFTER_SECONDS, result);
  }

  @Test
  void extractRetryAfter_shouldReturnZero_whenExceptionIsNotRateLimit() throws Exception {
    TelegramApiException genericException = new TelegramApiException("Generic error");

    Method method =
        rateLimitedClient.getClass().getDeclaredMethod(EXTRACT_RETRY_AFTER_METHOD, Throwable.class);
    method.setAccessible(true);

    Integer result = (Integer) method.invoke(rateLimitedClient, genericException);

    assertEquals(NO_RETRY_DELAY, result);
  }

  @Test
  void extractRetryAfter_shouldExtractFromCause() throws Exception {
    TelegramApiRequestException rootCause = mock(TelegramApiRequestException.class);
    ResponseParameters params = mock(ResponseParameters.class);

    when(rootCause.getErrorCode()).thenReturn(RATE_LIMIT_CODE);
    when(rootCause.getParameters()).thenReturn(params);
    when(params.getRetryAfter()).thenReturn(RETRY_AFTER_SECONDS);

    TelegramApiException wrapperException = new TelegramApiException("Wrapper", rootCause);

    Method method =
        rateLimitedClient.getClass().getDeclaredMethod(EXTRACT_RETRY_AFTER_METHOD, Throwable.class);
    method.setAccessible(true);

    Integer result = (Integer) method.invoke(rateLimitedClient, wrapperException);

    assertEquals(RETRY_AFTER_SECONDS, result);
  }

  @Test
  void waitFor_shouldThrowTelegramApiException_whenInterrupted() throws Exception {
    int executionDelay = 1;
    Method method = rateLimitedClient.getClass().getDeclaredMethod(WAIT_FOR_METHOD, int.class);
    method.setAccessible(true);

    Thread.currentThread().interrupt();

    InvocationTargetException ex =
        assertThrows(
            InvocationTargetException.class,
            () -> method.invoke(rateLimitedClient, executionDelay));

    assertInstanceOf(TelegramApiException.class, ex.getCause());
    assertEquals("Interrupted during rate limit wait", ex.getCause().getMessage());
  }

  @Test
  void execute_shouldNotRetry_whenErrorIsNot429() throws TelegramApiException {
    TelegramApiException genericException = new TelegramApiException("Generic error");

    when(delegate.execute(sendMessage)).thenThrow(genericException);

    assertThrows(TelegramApiException.class, () -> rateLimitedClient.execute(sendMessage));

    verify(delegate).execute(sendMessage);
    verify(delegate, never()).execute((SendDocument) any());
  }

  @Test
  void allExecuteMethodsCoverage_shouldVerifyExecutionForEachMethod_whenCalled()
      throws TelegramApiException {
    Object[] methods = getExecutionTestMethods();

    for (Object m : methods) {
      verifyMethodExecution(m);
    }
  }

  private Object[] getChatIdMethodsMocks() {
    return new Object[] {
      mock(SendVideo.class),
      mock(SendDocument.class),
      mock(SendAudio.class),
      mock(SendVoice.class),
      mock(SendVideoNote.class),
      mock(SendSticker.class),
      mock(SendAnimation.class),
      mock(SendLocation.class),
      mock(SendVenue.class),
      mock(SendContact.class),
      mock(SendPoll.class),
      mock(SendDice.class),
      mock(SendMediaGroup.class),
      mock(SendPaidMedia.class),
      mock(SendInvoice.class),
      mock(SendGame.class),
      mock(ForwardMessage.class),
      mock(CopyMessage.class),
      mock(EditMessageText.class),
      mock(EditMessageCaption.class),
      mock(EditMessageReplyMarkup.class),
      mock(EditMessageMedia.class),
      mock(EditMessageLiveLocation.class),
      mock(StopMessageLiveLocation.class),
      mock(DeleteMessage.class),
      mock(DeleteMessages.class),
      mock(StopPoll.class),
      mock(SendChatAction.class),
      mock(SetChatPhoto.class),
      mock(DeleteChatPhoto.class),
      mock(SetChatTitle.class),
      mock(SetChatDescription.class),
      mock(PinChatMessage.class),
      mock(UnpinChatMessage.class),
      mock(UnpinAllChatMessages.class),
      mock(BanChatMember.class),
      mock(UnbanChatMember.class),
      mock(RestrictChatMember.class),
      mock(PromoteChatMember.class),
      mock(SetChatAdministratorCustomTitle.class),
      mock(BanChatSenderChat.class),
      mock(UnbanChatSenderChat.class),
      mock(SetChatPermissions.class),
      mock(ExportChatInviteLink.class),
      mock(CreateChatInviteLink.class),
      mock(EditChatInviteLink.class),
      mock(RevokeChatInviteLink.class),
      mock(ApproveChatJoinRequest.class),
      mock(DeclineChatJoinRequest.class),
      mock(SetMessageReaction.class),
      mock(CreateForumTopic.class),
      mock(EditForumTopic.class),
      mock(CloseForumTopic.class),
      mock(ReopenForumTopic.class),
      mock(DeleteForumTopic.class),
      mock(UnpinAllForumTopicMessages.class),
      mock(EditGeneralForumTopic.class),
      mock(SetChatStickerSet.class),
      mock(DeleteChatStickerSet.class)
    };
  }

  private Object[] getExecutionTestMethods() {
    return new Object[] {
      new SendMessage(CHAT_ID, MESSAGE_TEXT),
      SendDocument.builder().chatId(CHAT_ID).document(new InputFile()).build(),
      SendPhoto.builder().chatId(CHAT_ID).photo(new InputFile()).build(),
      SendVideo.builder().chatId(CHAT_ID).video(new InputFile()).build(),
      SendVideoNote.builder().chatId(CHAT_ID).videoNote(new InputFile()).build(),
      SendSticker.builder().chatId(CHAT_ID).sticker(new InputFile()).build(),
      SendAudio.builder().chatId(CHAT_ID).audio(new InputFile()).build(),
      SendVoice.builder().chatId(CHAT_ID).voice(new InputFile()).build(),
      SendMediaGroup.builder().chatId(CHAT_ID).medias(java.util.Collections.emptyList()).build(),
      SendPaidMedia.builder()
          .chatId(CHAT_ID)
          .starCount(TEST_STAR_COUNT)
          .media(java.util.Collections.emptyList())
          .build(),
      SendAnimation.builder().chatId(CHAT_ID).animation(new InputFile()).build(),
      EditMessageMedia.builder()
          .chatId(CHAT_ID)
          .messageId(TEST_MESSAGE_ID)
          .media(mock(org.telegram.telegrambots.meta.api.objects.media.InputMedia.class))
          .build(),
      SetChatPhoto.builder().chatId(CHAT_ID).photo(new InputFile()).build(),
      SetWebhook.builder().url(TEST_URL).build(),
      SetBusinessAccountProfilePhoto.builder()
          .businessConnectionId(BIZ_CONN_ID)
          .photo(InputProfilePhotoStatic.builder().photo(new InputFile()).build())
          .build(),
      new SetMyProfilePhoto(),
      AddStickerToSet.builder()
          .userId(TEST_USER_ID)
          .name(TEST_STICKER_NAME)
          .sticker(mock(org.telegram.telegrambots.meta.api.objects.stickers.InputSticker.class))
          .build(),
      ReplaceStickerInSet.builder()
          .userId(TEST_USER_ID)
          .name(TEST_STICKER_NAME)
          .oldSticker("old")
          .sticker(mock(org.telegram.telegrambots.meta.api.objects.stickers.InputSticker.class))
          .build(),
      SetStickerSetThumbnail.builder()
          .userId(TEST_USER_ID)
          .name(TEST_STICKER_NAME)
          .format(TEST_STICKER_FORMAT)
          .thumbnail(new InputFile())
          .build(),
      CreateNewStickerSet.builder()
          .userId(TEST_USER_ID)
          .name(TEST_STICKER_NAME)
          .title("title")
          .stickers(Collections.emptyList())
          .build(),
      UploadStickerFile.builder()
          .userId(TEST_USER_ID)
          .sticker(new InputFile())
          .stickerFormat(TEST_STICKER_FORMAT)
          .build(),
      new org.telegram.telegrambots.meta.api.objects.File()
    };
  }

  private void verifyMethodExecution(Object m) throws TelegramApiException {
    switch (m) {
      case SendDocument doc -> {
        when(delegate.execute(doc)).thenReturn(mock(Message.class));
        when(delegate.executeAsync(doc))
            .thenReturn(CompletableFuture.completedFuture(mock(Message.class)));
        assertNotNull(rateLimitedClient.execute(doc));
        assertNotNull(rateLimitedClient.executeAsync(doc));
      }
      case SendPhoto photo -> {
        when(delegate.execute(photo)).thenReturn(mock(Message.class));
        when(delegate.executeAsync(photo))
            .thenReturn(CompletableFuture.completedFuture(mock(Message.class)));
        assertNotNull(rateLimitedClient.execute(photo));
        assertNotNull(rateLimitedClient.executeAsync(photo));
      }
      case SendVideo video -> {
        when(delegate.execute(video)).thenReturn(mock(Message.class));
        when(delegate.executeAsync(video))
            .thenReturn(CompletableFuture.completedFuture(mock(Message.class)));
        assertNotNull(rateLimitedClient.execute(video));
        assertNotNull(rateLimitedClient.executeAsync(video));
      }
      case SendVideoNote note -> {
        when(delegate.execute(note)).thenReturn(mock(Message.class));
        when(delegate.executeAsync(note))
            .thenReturn(CompletableFuture.completedFuture(mock(Message.class)));
        assertNotNull(rateLimitedClient.execute(note));
        assertNotNull(rateLimitedClient.executeAsync(note));
      }
      case SendSticker sticker -> {
        when(delegate.execute(sticker)).thenReturn(mock(Message.class));
        when(delegate.executeAsync(sticker))
            .thenReturn(CompletableFuture.completedFuture(mock(Message.class)));
        assertNotNull(rateLimitedClient.execute(sticker));
        assertNotNull(rateLimitedClient.executeAsync(sticker));
      }
      case SendAudio audio -> {
        when(delegate.execute(audio)).thenReturn(mock(Message.class));
        when(delegate.executeAsync(audio))
            .thenReturn(CompletableFuture.completedFuture(mock(Message.class)));
        assertNotNull(rateLimitedClient.execute(audio));
        assertNotNull(rateLimitedClient.executeAsync(audio));
      }
      case SendVoice voice -> {
        when(delegate.execute(voice)).thenReturn(mock(Message.class));
        when(delegate.executeAsync(voice))
            .thenReturn(CompletableFuture.completedFuture(mock(Message.class)));
        assertNotNull(rateLimitedClient.execute(voice));
        assertNotNull(rateLimitedClient.executeAsync(voice));
      }
      case SendMediaGroup group -> {
        when(delegate.execute(group)).thenReturn(List.of());
        when(delegate.executeAsync(group)).thenReturn(CompletableFuture.completedFuture(List.of()));
        assertNotNull(rateLimitedClient.execute(group));
        assertNotNull(rateLimitedClient.executeAsync(group));
      }
      case SendPaidMedia paid -> {
        when(delegate.execute(paid)).thenReturn(List.of());
        when(delegate.executeAsync(paid)).thenReturn(CompletableFuture.completedFuture(List.of()));
        assertNotNull(rateLimitedClient.execute(paid));
        assertNotNull(rateLimitedClient.executeAsync(paid));
      }
      case SendAnimation anim -> {
        when(delegate.execute(anim)).thenReturn(mock(Message.class));
        when(delegate.executeAsync(anim))
            .thenReturn(CompletableFuture.completedFuture(mock(Message.class)));
        assertNotNull(rateLimitedClient.execute(anim));
        assertNotNull(rateLimitedClient.executeAsync(anim));
      }
      case EditMessageMedia edit -> {
        when(delegate.execute(edit)).thenReturn(mock(Serializable.class));
        when(delegate.executeAsync(edit))
            .thenReturn(CompletableFuture.completedFuture(mock(Serializable.class)));
        assertNotNull(rateLimitedClient.execute(edit));
        assertNotNull(rateLimitedClient.executeAsync(edit));
      }
      case SetChatPhoto setChatPhoto -> {
        when(delegate.execute(setChatPhoto)).thenReturn(true);
        when(delegate.executeAsync(setChatPhoto))
            .thenReturn(CompletableFuture.completedFuture(true));
        assertNotNull(rateLimitedClient.execute(setChatPhoto));
        assertNotNull(rateLimitedClient.executeAsync(setChatPhoto));
      }
      case SetWebhook web -> {
        when(delegate.execute(web)).thenReturn(true);
        when(delegate.executeAsync(web)).thenReturn(CompletableFuture.completedFuture(true));
        assertNotNull(rateLimitedClient.execute(web));
        assertNotNull(rateLimitedClient.executeAsync(web));
      }
      case SetBusinessAccountProfilePhoto bizPhoto -> {
        when(delegate.execute(bizPhoto)).thenReturn(true);
        when(delegate.executeAsync(bizPhoto)).thenReturn(CompletableFuture.completedFuture(true));
        assertNotNull(rateLimitedClient.execute(bizPhoto));
        assertNotNull(rateLimitedClient.executeAsync(bizPhoto));
      }
      case SetMyProfilePhoto myPhoto -> {
        when(delegate.execute(myPhoto)).thenReturn(true);
        when(delegate.executeAsync(myPhoto)).thenReturn(CompletableFuture.completedFuture(true));
        assertNotNull(rateLimitedClient.execute(myPhoto));
        assertNotNull(rateLimitedClient.executeAsync(myPhoto));
      }
      case AddStickerToSet addSticker -> {
        when(delegate.execute(addSticker)).thenReturn(true);
        when(delegate.executeAsync(addSticker)).thenReturn(CompletableFuture.completedFuture(true));
        assertNotNull(rateLimitedClient.execute(addSticker));
        assertNotNull(rateLimitedClient.executeAsync(addSticker));
      }
      case ReplaceStickerInSet repSticker -> {
        when(delegate.execute(repSticker)).thenReturn(true);
        when(delegate.executeAsync(repSticker)).thenReturn(CompletableFuture.completedFuture(true));
        assertNotNull(rateLimitedClient.execute(repSticker));
        assertNotNull(rateLimitedClient.executeAsync(repSticker));
      }
      case SetStickerSetThumbnail thumb -> {
        when(delegate.execute(thumb)).thenReturn(true);
        when(delegate.executeAsync(thumb)).thenReturn(CompletableFuture.completedFuture(true));
        assertNotNull(rateLimitedClient.execute(thumb));
        assertNotNull(rateLimitedClient.executeAsync(thumb));
      }
      case CreateNewStickerSet createSticker -> {
        when(delegate.execute(createSticker)).thenReturn(true);
        when(delegate.executeAsync(createSticker))
            .thenReturn(CompletableFuture.completedFuture(true));
        assertNotNull(rateLimitedClient.execute(createSticker));
        assertNotNull(rateLimitedClient.executeAsync(createSticker));
      }
      case UploadStickerFile upload -> {
        when(delegate.execute(upload)).thenReturn(telegramApiFile);
        when(delegate.executeAsync(upload))
            .thenReturn(CompletableFuture.completedFuture(telegramApiFile));
        assertNotNull(rateLimitedClient.execute(upload));
        assertNotNull(rateLimitedClient.executeAsync(upload));
      }
      case SendMessage msg -> {
        when(delegate.execute(msg)).thenReturn(message);
        when(delegate.executeAsync(msg)).thenReturn(CompletableFuture.completedFuture(message));
        assertNotNull(rateLimitedClient.execute(msg));
        assertNotNull(rateLimitedClient.executeAsync(msg));
      }
      case org.telegram.telegrambots.meta.api.objects.File file -> {
        when(delegate.downloadFile(file)).thenReturn(ioFile);
        when(delegate.downloadFileAsStream(file)).thenReturn(inputStream);
        when(delegate.downloadFileAsync(file))
            .thenReturn(CompletableFuture.completedFuture(ioFile));
        when(delegate.downloadFileAsStreamAsync(file))
            .thenReturn(CompletableFuture.completedFuture(inputStream));
        assertNotNull(rateLimitedClient.downloadFile(file));
        assertNotNull(rateLimitedClient.downloadFileAsStream(file));
        assertNotNull(rateLimitedClient.downloadFileAsync(file));
        assertNotNull(rateLimitedClient.downloadFileAsStreamAsync(file));
      }
      default -> throw new IllegalStateException("Unexpected value: " + m);
    }
  }
}
