package com.github.jawisimo.botengine.core;

import com.github.jawisimo.botengine.config.bot.BotProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LongPollingBotTest {

  private static final String TOKEN = "token";

  @Mock private BotProperties properties;
  @Mock private UpdateConsumer updateConsumer;

  @Test
  void getBotToken_shouldReturnTokenFromProperties_whenInvoked() {
    when(properties.token()).thenReturn(TOKEN);

    LongPollingBot bot = new LongPollingBot(properties, updateConsumer);

    String result = bot.getBotToken();

    assertEquals(TOKEN, result);
  }

  @Test
  void getUpdatesConsumer_shouldReturnUpdateConsumer_whenInvoked() {
    LongPollingBot bot = new LongPollingBot(properties, updateConsumer);

    LongPollingUpdateConsumer result = bot.getUpdatesConsumer();

    assertEquals(updateConsumer, result);
  }
}
