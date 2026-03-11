package io.github.jawisimo.botsengine.interaction.command;

import io.github.jawisimo.botsengine.interaction.command.handler.CommandHandler;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Executes registered command handlers when a matching command is detected.
 *
 * <p>Iterates through available {@link CommandHandler} implementations and delegates execution to
 * the first matching handler.
 *
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
public class CommandExecutor {

  private final List<CommandHandler> commandHandlers;

  /**
   * Executes a command if a corresponding handler exists.
   *
   * @param chatId the chat identifier
   * @param userInput the user input to evaluate
   * @return {@code true} if a command was executed, {@code false} otherwise
   */
  public boolean executeIfExists(String chatId, String userInput) {
    if (userInput == null) {
      return false;
    }

    for (CommandHandler handler : commandHandlers) {
      if (userInput.equalsIgnoreCase(handler.getCommandKey())) {
        handler.handle(chatId);
        return true;
      }
    }

    return false;
  }
}
