package com.github.jawisimo.botengine.service;

import com.github.jawisimo.botengine.interaction.DialogExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
@Slf4j
@RequiredArgsConstructor
public class UpdateService {
  private final DialogExecutor executor;

  @Async("asyncBotVirtualExecutor")
  public void onUpdateReceived(Update update) {
    try {
      if (update.hasMessage()) {
        executor.executeMessage(update.getMessage());
      } else if (update.hasCallbackQuery()) {
        executor.executeCallback(update.getCallbackQuery());
      } else {
        log.warn("Unsupported update type: {}", update);
      }
    } catch (Exception e) {
      log.error("An error occurred during update processing: {}", e.getMessage(), e);
    }
  }
}
