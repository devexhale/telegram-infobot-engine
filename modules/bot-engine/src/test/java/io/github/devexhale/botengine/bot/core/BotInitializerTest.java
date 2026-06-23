package io.github.devexhale.botengine.bot.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import io.github.devexhale.botengine.execution.common.command.CommandsInitializer;
import io.github.devexhale.botengine.properties.BotProperties;
import io.github.devexhale.botengine.testsupport.logger.TestLogCaptor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.ApplicationArguments;

@ExtendWith(MockitoExtension.class)
class BotInitializerTest {

  private static final String BOT_NAME = "MyBot";
  private static final String LOG_MSG_PREFIX = "Telegram bot ";
  private static final String LOG_MSG_SUFFIX = " initialized and ready";

  @Mock private BotProperties properties;
  @Mock private CommandsInitializer commandsInitializer;
  @Mock private ApplicationArguments applicationArguments;

  @Test
  void run_shouldInitializeCommandsAndLogReadyMessage() {
    when(properties.name()).thenReturn(BOT_NAME);

    BotInitializer botInitializer = new BotInitializer(properties, commandsInitializer);

    try (TestLogCaptor logCaptor = new TestLogCaptor(BotInitializer.class)) {
      botInitializer.run(applicationArguments);

      verify(commandsInitializer).setUpCommands();

      ILoggingEvent event = logCaptor.events().getFirst();
      assertEquals(Level.INFO, event.getLevel());
      assertTrue(event.getFormattedMessage().contains(LOG_MSG_PREFIX));
      assertTrue(event.getFormattedMessage().contains(LOG_MSG_SUFFIX));
      assertTrue(event.getFormattedMessage().contains(BOT_NAME));
    }
  }
}
