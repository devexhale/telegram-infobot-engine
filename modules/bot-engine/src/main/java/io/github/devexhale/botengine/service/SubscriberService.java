package io.github.devexhale.botengine.service;

import io.github.devexhale.botengine.repository.message.MessageCleanupRepository;
import io.github.devexhale.botengine.repository.subscribe.SubscriberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Manages user subscriptions to the bot.
 *
 * <p>Handles subscribing and unsubscribing users, and provides access to the list of all active
 * subscribers. Unsubscribing also cleans up the user's state and message history.
 *
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriberService {

  private final SubscriberRepository subscriberRepository;
  private final UserStateService userStateService;
  private final MessageCleanupRepository messageCleanupRepository;

  /**
   * Subscribes the specified chat to the bot.
   *
   * @param chatId the chat identifier to subscribe
   */
  public void subscribe(String chatId) {
    subscriberRepository.save(chatId);
    log.info("User subscribed. ChatID={}", chatId);
  }

  /**
   * Unsubscribes the specified chat from the bot and cleans up associated data.
   *
   * @param chatId the chat identifier to unsubscribe
   */
  public void unsubscribe(String chatId) {
    subscriberRepository.delete(chatId);
    userStateService.deleteUserState(chatId);
    messageCleanupRepository.deleteAllByChatId(chatId);
    log.info("User unsubscribed. ChatID={}", chatId);
  }

  /**
   * Retrieves all currently subscribed chat identifiers.
   *
   * @return a set of all subscriber chat IDs
   */
  public Set<String> getAllSubscribers() {
    return subscriberRepository.getAll();
  }
}
