package io.github.devexhale.botengine.execution.common.command.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import io.github.devexhale.botengine.domain.dialog.DialogNode;
import io.github.devexhale.botengine.execution.common.command.commandset.LastCommand;
import io.github.devexhale.botengine.execution.common.command.commandset.StartCommand;
import io.github.devexhale.botengine.execution.dialog.navigator.DialogNodeNavigator;
import io.github.devexhale.botengine.service.UserStateService;
import io.github.devexhale.botengine.storage.definition.DefinitionStorage;
import io.github.devexhale.botengine.testsupport.logger.TestLogCaptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LastCommandHandlerTest {

  private static final String CHAT_ID = "123456789";
  private static final String SAVED_NODE_KEY = "saved_node_key";

  @Mock private DefinitionStorage<DialogNode> dialogStorage;
  @Mock private UserStateService userStateService;
  @Mock private DialogNodeNavigator nodeNavigator;
  @Mock private DialogNode lastNode;

  private LastCommandHandler handler;

  @BeforeEach
  void setUp() {
    handler = new LastCommandHandler(dialogStorage, userStateService, nodeNavigator);
  }

  @Test
  void getCommandKey_returnsLastCommandName() {
    assertEquals(LastCommand.COMMAND_NAME, handler.getCommandKey());
  }

  @Test
  void handle_shouldNavigateToSavedNode_whenNodeExists() {
    when(userStateService.getUserStateOrDefault(CHAT_ID, StartCommand.COMMAND_NAME))
        .thenReturn(SAVED_NODE_KEY);
    when(dialogStorage.getNode(SAVED_NODE_KEY)).thenReturn(lastNode);

    handler.handle(CHAT_ID);

    verify(nodeNavigator).navigateMessage(CHAT_ID, SAVED_NODE_KEY);
  }

  @Test
  void handle_shouldNavigateToStartNode_whenNoSavedNodeExists() {
    when(userStateService.getUserStateOrDefault(CHAT_ID, StartCommand.COMMAND_NAME))
        .thenReturn(StartCommand.COMMAND_NAME);
    when(dialogStorage.getNode(StartCommand.COMMAND_NAME)).thenReturn(lastNode);

    handler.handle(CHAT_ID);

    verify(nodeNavigator).navigateMessage(CHAT_ID, StartCommand.COMMAND_NAME);
  }

  @Test
  void handle_shouldLogWarning_whenNodeForKeyReturnedByUserServiceDoesNotExist() {
    when(userStateService.getUserStateOrDefault(CHAT_ID, StartCommand.COMMAND_NAME))
        .thenReturn(SAVED_NODE_KEY);
    when(dialogStorage.getNode(SAVED_NODE_KEY)).thenReturn(null);

    try (TestLogCaptor logCaptor = new TestLogCaptor(AbstractCommandHandler.class)) {
      handler.handle(CHAT_ID);

      verify(nodeNavigator, never()).navigateMessage(any(), any());

      ILoggingEvent loggedEvent = logCaptor.events().getFirst();
      assertEquals(Level.WARN, loggedEvent.getLevel());
      assertTrue(
          loggedEvent
              .getFormattedMessage()
              .contains("Dialog node '" + SAVED_NODE_KEY + "' not found"));
      assertTrue(loggedEvent.getFormattedMessage().contains(CHAT_ID));
    }
  }
}
