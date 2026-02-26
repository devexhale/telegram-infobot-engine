package com.github.jawisimo.botengine.core;

import com.github.jawisimo.botengine.config.BotProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;

@Component
@RequiredArgsConstructor
public class TelegramBot implements SpringLongPollingBot {

  private final BotProperties properties;
  private final UpdateConsumer updateConsumer;

  @Override
  public String getBotToken() {
    return properties.token();
  }

  @Override
  public LongPollingUpdateConsumer getUpdatesConsumer() {
    return updateConsumer;
  }
}
