package io.github.devexhale.botengine.bot.core;

import io.github.devexhale.botengine.execution.common.command.CommandsInitializer;
import io.github.devexhale.botengine.properties.BotProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Performs startup initialization tasks for the Telegram bot.
 *
 * <p>Runs after the Spring context is fully initialized via {@link ApplicationRunner}. Registers
 * bot commands using {@link CommandsInitializer}.
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
   * <p>Registers bot commands and logs a successful initialization message.
   */
  @Override
  public void run(@NotNull ApplicationArguments args) {
    commandsInitializer.setUpCommands();
    log.info("Telegram bot {} initialized and ready", properties.name());
  }
}
