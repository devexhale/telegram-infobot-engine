package io.github.devexhale.botengine.execution.common.command;

import io.github.devexhale.botengine.execution.common.command.handler.CommandHandler;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Dispatches user commands to their corresponding {@link CommandHandler} implementations.
 *
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
public class CommandExecutor {

  private final List<CommandHandler> commandHandlers;

  /**
   * Attempts to execute a command matching the user input.
   *
   * @param chatId the chat identifier
   * @param userInput the user input to evaluate
   * @return {@code true} if a matching command was found and executed, {@code false} otherwise
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
