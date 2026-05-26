package io.github.devexhale.botengine.execution.common.command;

import io.github.devexhale.botengine.execution.common.command.commandset.Command;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

/**
 * Registers bot commands in Telegram during application startup.
 *
 * <p>Collects all {@link Command} beans and sends them to Telegram using {@link TelegramClient}.
 *
 * @since 1.0
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class CommandsInitializer {

  private final TelegramClient client;
  private final List<Command> commands;

  /** Sends configured bot commands to Telegram. */
  public void setUpCommands() {
    List<BotCommand> botCommands = commands.stream().map(Command::getCommand).toList();

    SetMyCommands setMyCommands = new SetMyCommands(botCommands);
    setMyCommands.setScope(new BotCommandScopeDefault());

    try {
      client.execute(setMyCommands);
      log.info("Bot commands successfully set: {}", botCommands);
    } catch (TelegramApiException e) {
      log.error("Failed to set bot commands: {}", e.getMessage());
    }
  }
}
