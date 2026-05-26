package io.github.devexhale.botengine.service;

import io.github.devexhale.botengine.repository.message.MessageCleanupRepository;
import io.github.devexhale.botengine.repository.subscribe.SubscriberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriberService {

  private final SubscriberRepository subscriberRepository;
  private final UserStateService userStateService;
  private final MessageCleanupRepository messageCleanupRepository;

  public void subscribe(String chatId) {
    subscriberRepository.add(chatId);
    log.info("User subscribed. ChatID={}", chatId);
  }

  public void unsubscribe(String chatId) {
    subscriberRepository.delete(chatId);
    userStateService.deleteUserState(chatId);
    messageCleanupRepository.deleteAllByChatId(chatId);
    log.info("User unsubscribed. ChatID={}", chatId);
  }

  public Set<String> getAllSubscribers() {
    return subscriberRepository.getAll();
  }
}
