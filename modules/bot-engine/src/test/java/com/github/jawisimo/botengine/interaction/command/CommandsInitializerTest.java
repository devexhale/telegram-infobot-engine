package com.github.jawisimo.botengine.interaction.command;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.github.jawisimo.botengine.interaction.command.commandset.Command;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@ExtendWith(MockitoExtension.class)
class CommandsInitializerTest {

  private static final String COMMAND_1 = "start";
  private static final String COMMAND_2 = "help";
  private static final String DESC_1 = "Start";
  private static final String DESC_2 = "Help";

  private static final String SUCCESS_LOG = "Bot commands successfully set:";
  private static final String FAIL_LOG = "Failed to set bot commands:";
  private static final String ERROR_MSG = "Something went wrong...";

  @Mock private TelegramClient client;
  @Mock private Command command1;
  @Mock private Command command2;

  private CommandsInitializer initializer;

  @BeforeEach
  void setUp() {
    initializer = new CommandsInitializer(client, List.of(command1, command2));
  }

  @Test
  void setUpCommands_shouldExecuteAndLogInfo_whenSuccess() throws TelegramApiException {
    ListAppender<ILoggingEvent> listAppender = getListAppender();

    BotCommand bc1 = new BotCommand(COMMAND_1, DESC_1);
    BotCommand bc2 = new BotCommand(COMMAND_2, DESC_2);

    when(command1.getCommand()).thenReturn(bc1);
    when(command2.getCommand()).thenReturn(bc2);

    ArgumentCaptor<SetMyCommands> captor = ArgumentCaptor.forClass(SetMyCommands.class);

    initializer.setUpCommands();

    verify(client).execute(captor.capture());
    SetMyCommands request = captor.getValue();

    assertEquals(List.of(bc1, bc2), request.getCommands());
    assertInstanceOf(BotCommandScopeDefault.class, request.getScope());

    ILoggingEvent event = listAppender.list.getFirst();
    assertEquals(Level.INFO, event.getLevel());
    assertTrue(event.getFormattedMessage().contains(SUCCESS_LOG));
    assertTrue(event.getFormattedMessage().contains(COMMAND_1));
    assertTrue(event.getFormattedMessage().contains(COMMAND_2));
  }

  @Test
  void setUpCommands_shouldLogErrorAndNotThrow_whenTelegramApiExceptionOccurs()
      throws TelegramApiException {
    ListAppender<ILoggingEvent> listAppender = getListAppender();

    when(command1.getCommand()).thenReturn(new BotCommand(COMMAND_1, DESC_1));
    when(command2.getCommand()).thenReturn(new BotCommand(COMMAND_2, DESC_2));

    doThrow(new TelegramApiException(ERROR_MSG)).when(client).execute(any(SetMyCommands.class));

    assertDoesNotThrow(() -> initializer.setUpCommands());

    ILoggingEvent event = listAppender.list.getFirst();
    assertEquals(Level.ERROR, event.getLevel());
    assertTrue(event.getFormattedMessage().contains(FAIL_LOG));
    assertTrue(event.getFormattedMessage().contains(ERROR_MSG));
  }

  private ListAppender<ILoggingEvent> getListAppender() {
    Logger logger = (Logger) LoggerFactory.getLogger(CommandsInitializer.class);
    ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
    listAppender.start();
    logger.addAppender(listAppender);
    return listAppender;
  }
}
