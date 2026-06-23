package io.github.devexhale.botengine.bot.rate;

import java.io.InputStream;
import java.io.Serializable;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.CopyMessage;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.SetMyProfilePhoto;
import org.telegram.telegrambots.meta.api.methods.StopMessageLiveLocation;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
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
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
@Primary
@Slf4j
public class RateLimitedTelegramClient implements TelegramClient {

  private static final int MAX_RETRIES = 3;
  private static final int INITIAL_ATTEMPT = 1;
  private static final int DEFAULT_RETRY_AFTER = 5;
  private static final int RATE_LIMIT_ERROR_CODE = 429;
  private static final int NO_RETRY_DELAY = 0;

  private final RateLimiter rateLimiter;
  private final TelegramClient delegate;
  private final ScheduledExecutorService scheduledExecutorService;

  public RateLimitedTelegramClient(
      RateLimiter rateLimiter,
      @Qualifier("telegramClient") TelegramClient delegate,
      ScheduledExecutorService scheduledExecutorService) {
    this.rateLimiter = rateLimiter;
    this.delegate = delegate;
    this.scheduledExecutorService = scheduledExecutorService;
  }

  @FunctionalInterface
  private interface SyncAction<T> {
    T execute() throws TelegramApiException;
  }

  @FunctionalInterface
  private interface AsyncAction<T> {
    CompletableFuture<T> execute() throws TelegramApiException;
  }

  @Override
  public <T extends Serializable, M extends BotApiMethod<T>> T execute(M m)
      throws TelegramApiException {
    return executeSync(resolveChatId(m), () -> delegate.execute(m));
  }

  @Override
  public Message execute(SendDocument m) throws TelegramApiException {
    return executeSync(m.getChatId(), () -> delegate.execute(m));
  }

  @Override
  public Message execute(SendPhoto m) throws TelegramApiException {
    return executeSync(m.getChatId(), () -> delegate.execute(m));
  }

  @Override
  public Message execute(SendVideo m) throws TelegramApiException {
    return executeSync(m.getChatId(), () -> delegate.execute(m));
  }

  @Override
  public Message execute(SendVideoNote m) throws TelegramApiException {
    return executeSync(m.getChatId(), () -> delegate.execute(m));
  }

  @Override
  public Message execute(SendSticker m) throws TelegramApiException {
    return executeSync(m.getChatId(), () -> delegate.execute(m));
  }

  @Override
  public Message execute(SendAudio m) throws TelegramApiException {
    return executeSync(m.getChatId(), () -> delegate.execute(m));
  }

  @Override
  public Message execute(SendVoice m) throws TelegramApiException {
    return executeSync(m.getChatId(), () -> delegate.execute(m));
  }

  @Override
  public List<Message> execute(SendMediaGroup m) throws TelegramApiException {
    return executeSync(m.getChatId(), () -> delegate.execute(m));
  }

  @Override
  public List<Message> execute(SendPaidMedia m) throws TelegramApiException {
    return executeSync(m.getChatId(), () -> delegate.execute(m));
  }

  @Override
  public Message execute(SendAnimation m) throws TelegramApiException {
    return executeSync(m.getChatId(), () -> delegate.execute(m));
  }

  @Override
  public Serializable execute(EditMessageMedia m) throws TelegramApiException {
    return executeSync(m.getChatId(), () -> delegate.execute(m));
  }

  @Override
  public Boolean execute(SetChatPhoto m) throws TelegramApiException {
    return executeSync(m.getChatId(), () -> delegate.execute(m));
  }

  @Override
  public Boolean execute(SetWebhook m) throws TelegramApiException {
    return executeSync(null, () -> delegate.execute(m));
  }

  @Override
  public Boolean execute(SetBusinessAccountProfilePhoto m) throws TelegramApiException {
    return executeSync(null, () -> delegate.execute(m));
  }

  @Override
  public Boolean execute(SetMyProfilePhoto m) throws TelegramApiException {
    return executeSync(null, () -> delegate.execute(m));
  }

  @Override
  public Boolean execute(AddStickerToSet m) throws TelegramApiException {
    return executeSync(null, () -> delegate.execute(m));
  }

  @Override
  public Boolean execute(ReplaceStickerInSet m) throws TelegramApiException {
    return executeSync(null, () -> delegate.execute(m));
  }

  @Override
  public Boolean execute(SetStickerSetThumbnail m) throws TelegramApiException {
    return executeSync(null, () -> delegate.execute(m));
  }

  @Override
  public Boolean execute(CreateNewStickerSet m) throws TelegramApiException {
    return executeSync(null, () -> delegate.execute(m));
  }

  @Override
  public File execute(UploadStickerFile m) throws TelegramApiException {
    return executeSync(null, () -> delegate.execute(m));
  }

  @Override
  public java.io.File downloadFile(File file) throws TelegramApiException {
    return executeSync(null, () -> delegate.downloadFile(file));
  }

  @Override
  public InputStream downloadFileAsStream(File file) throws TelegramApiException {
    return executeSync(null, () -> delegate.downloadFileAsStream(file));
  }

  @Override
  public <T extends Serializable, M extends BotApiMethod<T>> CompletableFuture<T> executeAsync(
      M m) {
    return executeAsyncWithRetry(resolveChatId(m), () -> delegate.executeAsync(m), INITIAL_ATTEMPT);
  }

  @Override
  public CompletableFuture<Message> executeAsync(SendDocument m) {
    return executeAsyncWithRetry(m.getChatId(), () -> delegate.executeAsync(m), INITIAL_ATTEMPT);
  }

  @Override
  public CompletableFuture<Message> executeAsync(SendPhoto m) {
    return executeAsyncWithRetry(m.getChatId(), () -> delegate.executeAsync(m), INITIAL_ATTEMPT);
  }

  @Override
  public CompletableFuture<Message> executeAsync(SendVideo m) {
    return executeAsyncWithRetry(m.getChatId(), () -> delegate.executeAsync(m), INITIAL_ATTEMPT);
  }

  @Override
  public CompletableFuture<Message> executeAsync(SendVideoNote m) {
    return executeAsyncWithRetry(m.getChatId(), () -> delegate.executeAsync(m), INITIAL_ATTEMPT);
  }

  @Override
  public CompletableFuture<Message> executeAsync(SendSticker m) {
    return executeAsyncWithRetry(m.getChatId(), () -> delegate.executeAsync(m), INITIAL_ATTEMPT);
  }

  @Override
  public CompletableFuture<Message> executeAsync(SendAudio m) {
    return executeAsyncWithRetry(m.getChatId(), () -> delegate.executeAsync(m), INITIAL_ATTEMPT);
  }

  @Override
  public CompletableFuture<Message> executeAsync(SendVoice m) {
    return executeAsyncWithRetry(m.getChatId(), () -> delegate.executeAsync(m), INITIAL_ATTEMPT);
  }

  @Override
  public CompletableFuture<List<Message>> executeAsync(SendMediaGroup m) {
    return executeAsyncWithRetry(m.getChatId(), () -> delegate.executeAsync(m), INITIAL_ATTEMPT);
  }

  @Override
  public CompletableFuture<List<Message>> executeAsync(SendPaidMedia m) {
    return executeAsyncWithRetry(m.getChatId(), () -> delegate.executeAsync(m), INITIAL_ATTEMPT);
  }

  @Override
  public CompletableFuture<Message> executeAsync(SendAnimation m) {
    return executeAsyncWithRetry(m.getChatId(), () -> delegate.executeAsync(m), INITIAL_ATTEMPT);
  }

  @Override
  public CompletableFuture<Serializable> executeAsync(EditMessageMedia m) {
    return executeAsyncWithRetry(m.getChatId(), () -> delegate.executeAsync(m), INITIAL_ATTEMPT);
  }

  @Override
  public CompletableFuture<Boolean> executeAsync(SetChatPhoto m) {
    return executeAsyncWithRetry(m.getChatId(), () -> delegate.executeAsync(m), INITIAL_ATTEMPT);
  }

  @Override
  public CompletableFuture<Boolean> executeAsync(SetWebhook m) {
    return executeAsyncWithRetry(null, () -> delegate.executeAsync(m), INITIAL_ATTEMPT);
  }

  @Override
  public CompletableFuture<Boolean> executeAsync(SetBusinessAccountProfilePhoto m) {
    return executeAsyncWithRetry(null, () -> delegate.executeAsync(m), INITIAL_ATTEMPT);
  }

  @Override
  public CompletableFuture<Boolean> executeAsync(SetMyProfilePhoto m) {
    return executeAsyncWithRetry(null, () -> delegate.executeAsync(m), INITIAL_ATTEMPT);
  }

  @Override
  public CompletableFuture<Boolean> executeAsync(AddStickerToSet m) {
    return executeAsyncWithRetry(null, () -> delegate.executeAsync(m), INITIAL_ATTEMPT);
  }

  @Override
  public CompletableFuture<Boolean> executeAsync(ReplaceStickerInSet m) {
    return executeAsyncWithRetry(null, () -> delegate.executeAsync(m), INITIAL_ATTEMPT);
  }

  @Override
  public CompletableFuture<Boolean> executeAsync(SetStickerSetThumbnail m) {
    return executeAsyncWithRetry(null, () -> delegate.executeAsync(m), INITIAL_ATTEMPT);
  }

  @Override
  public CompletableFuture<Boolean> executeAsync(CreateNewStickerSet m) {
    return executeAsyncWithRetry(null, () -> delegate.executeAsync(m), INITIAL_ATTEMPT);
  }

  @Override
  public CompletableFuture<File> executeAsync(UploadStickerFile m) {
    return executeAsyncWithRetry(null, () -> delegate.executeAsync(m), INITIAL_ATTEMPT);
  }

  @Override
  public CompletableFuture<java.io.File> downloadFileAsync(File file) {
    return executeAsyncWithRetry(null, () -> delegate.downloadFileAsync(file), INITIAL_ATTEMPT);
  }

  @Override
  public CompletableFuture<InputStream> downloadFileAsStreamAsync(File file) {
    return executeAsyncWithRetry(
        null, () -> delegate.downloadFileAsStreamAsync(file), INITIAL_ATTEMPT);
  }

  private <T> T executeSync(String chatId, SyncAction<T> action) throws TelegramApiException {
    rateLimiter.acquire(chatId);

    int attempt = INITIAL_ATTEMPT;

    while (true) {
      try {
        return action.execute();
      } catch (TelegramApiException e) {
        int retryAfter = extractRetryAfter(e);

        if (retryAfter > 0 && attempt < MAX_RETRIES) {
          log.warn(
              "Rate limited sync (429). Waiting {}s. Attempt {}/{}. ChatID={}",
              retryAfter,
              attempt,
              MAX_RETRIES,
              chatId);
          waitFor(retryAfter);
          attempt++;
        } else {
          throw e;
        }
      }
    }
  }

  private void waitFor(int seconds) throws TelegramApiException {
    try {
      Thread.sleep(TimeUnit.SECONDS.toMillis(seconds));
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();
      throw new TelegramApiException("Interrupted during rate limit wait", ie);
    }
  }

  private <T> CompletableFuture<T> executeAsyncWithRetry(
      String chatId, AsyncAction<T> action, int attempt) {

    try {
      if (attempt == INITIAL_ATTEMPT) {
        rateLimiter.acquire(chatId);
      }

      return action
          .execute()
          .exceptionallyCompose(throwable -> handleAsyncRetry(chatId, action, attempt, throwable));

    } catch (TelegramApiException e) {
      return handleAsyncRetry(chatId, action, attempt, e);
    } catch (Exception e) {
      return CompletableFuture.failedFuture(e);
    }
  }

  private <T> CompletableFuture<T> handleAsyncRetry(
      String chatId, AsyncAction<T> action, int attempt, Throwable throwable) {
    int retryAfter = extractRetryAfter(throwable);

    if (retryAfter <= 0 || attempt >= MAX_RETRIES) {
      return CompletableFuture.failedFuture(throwable);
    }

    log.warn(
        "Rate limited async (429). Scheduling retry in {}s. Attempt {}/{}. ChatID={}",
        retryAfter,
        attempt,
        MAX_RETRIES,
        chatId);
    return scheduleRetryTask(chatId, action, attempt, retryAfter);
  }

  private <T> CompletableFuture<T> scheduleRetryTask(
      String chatId, AsyncAction<T> action, int attempt, int retryAfter) {
    CompletableFuture<T> nextTry = new CompletableFuture<>();
    scheduledExecutorService.schedule(
        () ->
            executeAsyncWithRetry(chatId, action, attempt + 1)
                .whenComplete((res, err) -> propagateResult(nextTry, res, err)),
        retryAfter,
        TimeUnit.SECONDS);
    return nextTry;
  }

  private <T> void propagateResult(CompletableFuture<T> targetFuture, T result, Throwable error) {
    if (error != null) {
      targetFuture.completeExceptionally(error);
      return;
    }

    targetFuture.complete(result);
  }

  private int extractRetryAfter(Throwable t) {
    if (t == null) return NO_RETRY_DELAY;
    if (t instanceof TelegramApiRequestException apiEx
        && apiEx.getErrorCode() == RATE_LIMIT_ERROR_CODE) {
      return getRetrySecondsOrDefault(apiEx);
    }

    if (t.getCause() != null && t != t.getCause()) {
      return extractRetryAfter(t.getCause());
    }

    return NO_RETRY_DELAY;
  }

  private int getRetrySecondsOrDefault(TelegramApiRequestException apiEx) {
    if (apiEx.getParameters() != null && apiEx.getParameters().getRetryAfter() != null) {
      return apiEx.getParameters().getRetryAfter();
    }

    return DEFAULT_RETRY_AFTER;
  }

  @SuppressWarnings("java:S1479")
  private String resolveChatId(Object method) {
    return switch (method) {
      // === SEND MESSAGES ===
      case SendMessage m -> m.getChatId();
      case SendPhoto m -> m.getChatId();
      case SendVideo m -> m.getChatId();
      case SendDocument m -> m.getChatId();
      case SendAudio m -> m.getChatId();
      case SendVoice m -> m.getChatId();
      case SendVideoNote m -> m.getChatId();
      case SendSticker m -> m.getChatId();
      case SendAnimation m -> m.getChatId();
      case SendLocation m -> m.getChatId();
      case SendVenue m -> m.getChatId();
      case SendContact m -> m.getChatId();
      case SendPoll m -> m.getChatId();
      case SendDice m -> m.getChatId();
      case SendMediaGroup m -> m.getChatId();
      case SendPaidMedia m -> m.getChatId();
      case SendInvoice m -> m.getChatId();
      case SendGame m -> m.getChatId();

      // === FORWARD / COPY ===
      case ForwardMessage m -> m.getChatId();
      case CopyMessage m -> m.getChatId();

      // === EDIT / DELETE MESSAGES ===
      case EditMessageText m -> m.getChatId();
      case EditMessageCaption m -> m.getChatId();
      case EditMessageReplyMarkup m -> m.getChatId();
      case EditMessageMedia m -> m.getChatId();
      case EditMessageLiveLocation m -> m.getChatId();
      case StopMessageLiveLocation m -> m.getChatId();
      case DeleteMessage m -> m.getChatId();
      case DeleteMessages m -> m.getChatId();

      // === POLLS ===
      case StopPoll m -> m.getChatId();

      // === CHAT ACTIONS ===
      case SendChatAction m -> m.getChatId();

      // === CHAT ADMINISTRATION ===
      case SetChatPhoto m -> m.getChatId();
      case DeleteChatPhoto m -> m.getChatId();
      case SetChatTitle m -> m.getChatId();
      case SetChatDescription m -> m.getChatId();
      case PinChatMessage m -> m.getChatId();
      case UnpinChatMessage m -> m.getChatId();
      case UnpinAllChatMessages m -> m.getChatId();
      case BanChatMember m -> m.getChatId();
      case UnbanChatMember m -> m.getChatId();
      case RestrictChatMember m -> m.getChatId();
      case PromoteChatMember m -> m.getChatId();
      case SetChatAdministratorCustomTitle m -> m.getChatId();
      case BanChatSenderChat m -> m.getChatId();
      case UnbanChatSenderChat m -> m.getChatId();
      case SetChatPermissions m -> m.getChatId();

      // === INVITE LINKS ===
      case ExportChatInviteLink m -> m.getChatId();
      case CreateChatInviteLink m -> m.getChatId();
      case EditChatInviteLink m -> m.getChatId();
      case RevokeChatInviteLink m -> m.getChatId();
      case ApproveChatJoinRequest m -> m.getChatId();
      case DeclineChatJoinRequest m -> m.getChatId();

      // === REACTIONS ===
      case SetMessageReaction m -> m.getChatId();

      // === FORUM TOPICS ===
      case CreateForumTopic m -> m.getChatId();
      case EditForumTopic m -> m.getChatId();
      case CloseForumTopic m -> m.getChatId();
      case ReopenForumTopic m -> m.getChatId();
      case DeleteForumTopic m -> m.getChatId();
      case UnpinAllForumTopicMessages m -> m.getChatId();
      case EditGeneralForumTopic m -> m.getChatId();

      // === STICKERS (CHAT-LEVEL) ===
      case SetChatStickerSet m -> m.getChatId();
      case DeleteChatStickerSet m -> m.getChatId();

      // === DEFAULT: NO CHAT_ID ===
      default -> null;
    };
  }
}
