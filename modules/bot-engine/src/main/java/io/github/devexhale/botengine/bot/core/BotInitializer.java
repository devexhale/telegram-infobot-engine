package io.github.devexhale.botengine.bot.core;

import io.github.devexhale.botengine.execution.common.command.CommandsInitializer;
import io.github.devexhale.botengine.properties.BotProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Performs startup initialization tasks for the Telegram bot.
 *
 * <p>This component runs after the Spring application context is fully initialized using {@link
 * ApplicationRunner}. It performs bot startup steps such as registering commands via {@link
 * CommandsInitializer}.
 *
 * <p>Execution flow: ApplicationRunner -> BotInitializer -> CommandsInitializer -> Telegram API.
 *
 * @since 1.0
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class BotInitializer implements ApplicationRunner {

  private final BotProperties properties;
  private final CommandsInitializer commandsInitializer;

  /**
   * Executes bot initialization routines after application startup.
   *
   * <p>Registers bot commands and logs that the Telegram bot has been successfully initialized and
   * is ready to receive updates.
   */
  @Override
  public void run(ApplicationArguments args) {
    commandsInitializer.setUpCommands();
    log.info("Telegram bot {} initialized and ready", properties.name());
  }
}
