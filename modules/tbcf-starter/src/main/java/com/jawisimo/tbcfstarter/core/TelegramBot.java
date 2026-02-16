package com.jawisimo.tbcfstarter.core;

import com.jawisimo.tbcfstarter.config.BotProperties;
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
