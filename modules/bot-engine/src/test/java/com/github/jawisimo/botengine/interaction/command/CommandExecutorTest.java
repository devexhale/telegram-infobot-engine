package com.github.jawisimo.botengine.interaction.command;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.github.jawisimo.botengine.interaction.command.handler.CommandHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class CommandExecutorTest {
  private static final String CHAT_ID = "123456789";
  private static final String START_COMMAND = "/start";
  private static final String LAST_COMMAND = "/last";
  private static final String UNKNOWN_COMMAND = "/unknown";

  @Mock private CommandHandler startHandler;
  @Mock private CommandHandler lastHandler;

  private CommandExecutor commandExecutor;

  @BeforeEach
  void setUp() {
    commandExecutor = new CommandExecutor(List.of(startHandler, lastHandler));
  }

  @Test
  void executeIfExists_shouldReturnFalse_whenUserInputIsNull() {
    boolean result = commandExecutor.executeIfExists(CHAT_ID, null);

    assertFalse(result);
    verifyNoInteractions(startHandler, lastHandler);
  }

  @Test
  void executeIfExists_shouldExecuteStartCommandAndReturnTrue() {
    when(startHandler.getCommandKey()).thenReturn(START_COMMAND);

    boolean result = commandExecutor.executeIfExists(CHAT_ID, START_COMMAND);

    assertTrue(result);
    verify(startHandler).handle(CHAT_ID);
    verify(lastHandler, never()).handle(anyString());
  }

  @Test
  void executeIfExists_shouldExecuteLastCommandAndReturnTrue() {
    when(lastHandler.getCommandKey()).thenReturn(LAST_COMMAND);

    boolean result = commandExecutor.executeIfExists(CHAT_ID, LAST_COMMAND);

    assertTrue(result);
    verify(lastHandler).handle(CHAT_ID);
    verify(startHandler, never()).handle(anyString());
  }

  @Test
  void executeIfExists_shouldReturnFalse_whenCommandNotFound() {
    when(startHandler.getCommandKey()).thenReturn(START_COMMAND);
    when(lastHandler.getCommandKey()).thenReturn(LAST_COMMAND);

    boolean result = commandExecutor.executeIfExists(CHAT_ID, UNKNOWN_COMMAND);

    assertFalse(result);
    verify(startHandler, never()).handle(anyString());
    verify(lastHandler, never()).handle(anyString());
  }

  @Test
  void executeIfExists_shouldStopSearchingAfterFindingCommand() {
    when(startHandler.getCommandKey()).thenReturn(START_COMMAND);

    boolean result = commandExecutor.executeIfExists(CHAT_ID, START_COMMAND);

    assertTrue(result);
    verify(startHandler).handle(CHAT_ID);
    verify(lastHandler, never()).handle(anyString());
    verify(lastHandler, never()).getCommandKey();
  }

  @Test
  void executeIfExists_shouldCheckAllHandlers_whenCommandNotFound() {
    when(startHandler.getCommandKey()).thenReturn(START_COMMAND);
    when(lastHandler.getCommandKey()).thenReturn(LAST_COMMAND);

    boolean result = commandExecutor.executeIfExists(CHAT_ID, UNKNOWN_COMMAND);

    assertFalse(result);
    verify(startHandler).getCommandKey();
    verify(lastHandler).getCommandKey();
    verify(startHandler, never()).handle(anyString());
    verify(lastHandler, never()).handle(anyString());
  }

  @Test
  void executeIfExists_shouldHandleEmptyHandlerList() {
    commandExecutor = new CommandExecutor(List.of());

    boolean result = commandExecutor.executeIfExists(CHAT_ID, START_COMMAND);

    assertFalse(result);
  }

  @ParameterizedTest
  @ValueSource(strings = {"/start", "/START", "/Start", "/STaRT"})
  void executeIfExists_shouldBeCaseInsensitive(String input) {
    when(startHandler.getCommandKey()).thenReturn(START_COMMAND);

    boolean result = commandExecutor.executeIfExists(CHAT_ID, input);

    assertTrue(result);
    verify(startHandler).handle(CHAT_ID);
    verify(lastHandler, never()).handle(anyString());
  }

  @Test
  void executeIfExists_shouldHandleWhitespaceInput() {
    when(startHandler.getCommandKey()).thenReturn(START_COMMAND);
    when(lastHandler.getCommandKey()).thenReturn(LAST_COMMAND);

    boolean result = commandExecutor.executeIfExists(CHAT_ID, "  ");

    assertFalse(result);
    verify(startHandler).getCommandKey();
    verify(lastHandler).getCommandKey();
    verify(startHandler, never()).handle(anyString());
    verify(lastHandler, never()).handle(anyString());
  }
}
