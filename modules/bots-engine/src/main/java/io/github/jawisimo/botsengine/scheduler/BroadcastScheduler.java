package io.github.jawisimo.botsengine.scheduler;

import io.github.jawisimo.botsengine.service.SubscriberService;

import java.time.LocalDateTime;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
@RequiredArgsConstructor
@Slf4j
public class BroadcastScheduler {

  private final TelegramClient client;
  private final SubscriberService subscriberService;

  @Scheduled(fixedRate = 30000)
  public void sendTestBroadcast() {
    Set<String> subscribers = subscriberService.getAll();

    if (subscribers.isEmpty()) {
      log.debug("No subscribers for broadcast");
      return;
    }

    String text = "Hello, Subscriber! " + "time: " + LocalDateTime.now();

    for (String chatId : subscribers) {
      try {
        client.executeAsync(new SendMessage(chatId, text));
      } catch (TelegramApiException e) {
        log.warn("Failed to send message to {}", chatId, e);
      }
    }
  }
}
