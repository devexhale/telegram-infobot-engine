package io.github.devexhale.botengine.execution.common.command;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import io.github.devexhale.botengine.execution.common.command.commandset.Command;
import io.github.devexhale.botengine.testsupport.logger.TestLogCaptor;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@ExtendWith(MockitoExtension.class)
class CommandsInitializerTest {

  private static final String COMMAND_1 = "/start";
  private static final String COMMAND_2 = "/help";
  private static final String DESC_1 = "Start";
  private static final String DESC_2 = "Help";

  private static final String SUCCESS_LOG = "Bot commands successfully set:";
  private static final String FAIL_LOG = "Failed to set bot commands:";
  private static final String ERROR_MSG = "Something went wrong...";

  @Mock private TelegramClient client;
  @Mock private Command command1;
  @Mock private Command command2;

  @Captor private ArgumentCaptor<SetMyCommands> setMyCommandsCaptor;

  private CommandsInitializer initializer;
  private TestLogCaptor logCaptor;

  @BeforeEach
  void setUp() {
    initializer = new CommandsInitializer(client, List.of(command1, command2));
    logCaptor = new TestLogCaptor(CommandsInitializer.class);
  }

  @AfterEach
  void tearDown() {
    logCaptor.close();
  }

  @Test
  void setUpCommands_shouldExecuteAndLogInfo_whenSuccess() throws TelegramApiException {
    BotCommand bc1 = new BotCommand(COMMAND_1, DESC_1);
    BotCommand bc2 = new BotCommand(COMMAND_2, DESC_2);

    when(command1.getCommand()).thenReturn(bc1);
    when(command2.getCommand()).thenReturn(bc2);

    initializer.setUpCommands();

    verify(client).execute(setMyCommandsCaptor.capture());
    SetMyCommands request = setMyCommandsCaptor.getValue();

    assertEquals(List.of(bc1, bc2), request.getCommands());
    assertInstanceOf(BotCommandScopeDefault.class, request.getScope());

    ILoggingEvent event = logCaptor.events().getFirst();
    assertEquals(Level.INFO, event.getLevel());
    assertTrue(event.getFormattedMessage().contains(SUCCESS_LOG));
    assertTrue(event.getFormattedMessage().contains(COMMAND_1));
    assertTrue(event.getFormattedMessage().contains(COMMAND_2));
  }

  @Test
  void setUpCommands_shouldLogErrorAndNotThrow_whenTelegramApiExceptionOccurs()
      throws TelegramApiException {
    when(command1.getCommand()).thenReturn(new BotCommand(COMMAND_1, DESC_1));
    when(command2.getCommand()).thenReturn(new BotCommand(COMMAND_2, DESC_2));

    doThrow(new TelegramApiException(ERROR_MSG)).when(client).execute(any(SetMyCommands.class));

    assertDoesNotThrow(() -> initializer.setUpCommands());

    ILoggingEvent event = logCaptor.events().getFirst();
    assertEquals(Level.ERROR, event.getLevel());
    assertTrue(event.getFormattedMessage().contains(FAIL_LOG));
    assertTrue(event.getFormattedMessage().contains(ERROR_MSG));
  }
}
