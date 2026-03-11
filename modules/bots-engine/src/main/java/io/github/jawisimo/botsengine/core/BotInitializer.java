package io.github.jawisimo.botsengine.core;

import io.github.jawisimo.botsengine.config.bot.BotProperties;
import io.github.jawisimo.botsengine.interaction.command.CommandsInitializer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Performs startup initialization tasks for the Telegram bot.
 *
 * <p>This component runs after the Spring application context is fully initialized. It performs bot
 * startup steps such as registering commands via {@link CommandsInitializer}.
 *
 * <p>Startup logic is executed after {@link ApplicationReadyEvent}, ensuring that all framework
 * components and infrastructure beans are fully constructed and ready for use.
 *
 * <p>Execution flow: ApplicationReadyEvent -> BotInitializer -> CommandsInitializer -> Telegram
 * API.
 *
 * @since 1.0
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class BotInitializer {

  private final CommandsInitializer commandsInitializer;
  private final BotProperties properties;

  /**
   * Handles the {@link ApplicationReadyEvent} and performs bot initialization routines.
   *
   * <p>Registers bot commands and logs that the Telegram bot has been successfully initialized and
   * is ready to receive updates.
   */
  @EventListener(ApplicationReadyEvent.class)
  public void onApplicationReady() {
    commandsInitializer.setUpCommands();
    log.info("Telegram bot '{}' initialized and ready", properties.name());
  }
}
