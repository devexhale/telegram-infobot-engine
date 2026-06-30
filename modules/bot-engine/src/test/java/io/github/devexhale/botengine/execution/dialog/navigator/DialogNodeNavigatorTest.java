package io.github.devexhale.botengine.execution.dialog.navigator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import io.github.devexhale.botengine.domain.dialog.Button;
import io.github.devexhale.botengine.domain.dialog.ButtonType;
import io.github.devexhale.botengine.domain.dialog.DialogNode;
import io.github.devexhale.botengine.execution.common.command.commandset.StartCommand;
import io.github.devexhale.botengine.service.UserStateService;
import io.github.devexhale.botengine.storage.definition.DefinitionStorage;
import io.github.devexhale.botengine.testsupport.logger.TestLogCaptor;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DialogNodeNavigatorTest {

  private static final String CHAT_ID = "123456789";
  private static final String USER_INPUT = "user_input";
  private static final String CALLBACK_DATA = "callback_data";
  private static final String EXISTING_NODE_ID = "existing_node";
  private static final String START_NODE_ID = StartCommand.COMMAND_NAME;
  private static final String IRRELEVANT_MSG_SENT = "Irrelevant message sent";
  private static final String MSG = "msg";

  @Mock private DefinitionStorage<DialogNode> dialogStorage;
  @Mock private DialogNodeExecutor dialogNodeExecutor;
  @Mock private UserStateService userStateService;

  @InjectMocks private DialogNodeNavigator dialogNodeNavigator;

  @Mock private DialogNode existingNode;
  @Mock private DialogNode startNode;

  private List<Button> replyButtons;
  private TestLogCaptor logCaptor;

  @BeforeEach
  void setUp() {
    Button matchingButton = new Button(USER_INPUT, EXISTING_NODE_ID, null);
    Button nonMatchingButton = new Button("other_label", "other_next", null);
    replyButtons = List.of(matchingButton, nonMatchingButton);
    logCaptor = new TestLogCaptor(DialogNodeNavigator.class);
  }

  @AfterEach
  void tearDown() {
    logCaptor.close();
  }

  @Test
  void navigateMessage_shouldExecutesNodeAndSavesState_whenNodeExistsForRawInput() {
    when(userStateService.getUserStateOrDefault(CHAT_ID, START_NODE_ID)).thenReturn(START_NODE_ID);
    when(dialogStorage.getNode(START_NODE_ID)).thenReturn(startNode);
    when(startNode.buttonType()).thenReturn(ButtonType.INLINE);
    when(dialogStorage.getNode(USER_INPUT)).thenReturn(existingNode);

    dialogNodeNavigator.navigateMessage(CHAT_ID, USER_INPUT);

    verify(dialogNodeExecutor).execute(existingNode, CHAT_ID);
    verify(userStateService).saveUserState(CHAT_ID, USER_INPUT);
  }

  @Test
  void navigateMessage_shouldLogWarnings_whenNodeDoesNotExistForRawInput() {
    when(userStateService.getUserStateOrDefault(CHAT_ID, START_NODE_ID)).thenReturn(START_NODE_ID);
    when(dialogStorage.getNode(START_NODE_ID)).thenReturn(startNode);
    when(startNode.buttonType()).thenReturn(ButtonType.INLINE);
    when(dialogStorage.getNode(USER_INPUT)).thenReturn(null);

    dialogNodeNavigator.navigateMessage(CHAT_ID, USER_INPUT);

    verify(dialogNodeExecutor, never()).execute(any(), any());
    verify(userStateService, never()).saveUserState(any(), any());

    ILoggingEvent loggedEvent = logCaptor.events().getFirst();
    assertEquals(Level.WARN, loggedEvent.getLevel());
    assertTrue(loggedEvent.getFormattedMessage().contains(IRRELEVANT_MSG_SENT));
    assertTrue(loggedEvent.getFormattedMessage().contains(USER_INPUT));
    assertTrue(loggedEvent.getFormattedMessage().contains(CHAT_ID));
  }

  @Test
  void navigateMessage_shouldExecutesNodeAndSavesState_whenNodeExistsForReplyButtonLabel() {
    DialogNode currentNodeWithReplyButtons =
        new DialogNode(null, MSG, ButtonType.REPLY, replyButtons);

    when(userStateService.getUserStateOrDefault(CHAT_ID, START_NODE_ID)).thenReturn(START_NODE_ID);
    when(dialogStorage.getNode(START_NODE_ID)).thenReturn(currentNodeWithReplyButtons);
    when(dialogStorage.getNode(EXISTING_NODE_ID)).thenReturn(existingNode);

    dialogNodeNavigator.navigateMessage(CHAT_ID, USER_INPUT);

    verify(dialogNodeExecutor).execute(existingNode, CHAT_ID);
    verify(userStateService).saveUserState(CHAT_ID, EXISTING_NODE_ID);
  }

  @Test
  void navigateMessage_shouldLogWarning_whenNodeDoesNotExistForReplyButtonLabel() {
    String nonMatchingButtonLabel = "non_matching_button_label";

    DialogNode currentNodeWithReplyButtons =
        new DialogNode(null, MSG, ButtonType.REPLY, replyButtons);

    when(userStateService.getUserStateOrDefault(CHAT_ID, START_NODE_ID)).thenReturn(START_NODE_ID);
    when(dialogStorage.getNode(START_NODE_ID)).thenReturn(currentNodeWithReplyButtons);

    dialogNodeNavigator.navigateMessage(CHAT_ID, nonMatchingButtonLabel);

    verify(dialogNodeExecutor, never()).execute(any(), any());
    verify(userStateService, never()).saveUserState(any(), any());

    ILoggingEvent loggedEvent = logCaptor.events().getFirst();
    assertEquals(Level.WARN, loggedEvent.getLevel());
    assertTrue(loggedEvent.getFormattedMessage().contains(IRRELEVANT_MSG_SENT));
    assertTrue(loggedEvent.getFormattedMessage().contains(nonMatchingButtonLabel));
    assertTrue(loggedEvent.getFormattedMessage().contains(CHAT_ID));
  }

  @Test
  void navigateMessage_shouldLogWarning_whenCurrentNodeIsNull() {
    when(userStateService.getUserStateOrDefault(CHAT_ID, START_NODE_ID)).thenReturn(START_NODE_ID);
    when(dialogStorage.getNode(START_NODE_ID)).thenReturn(null);

    dialogNodeNavigator.navigateMessage(CHAT_ID, USER_INPUT);

    verify(dialogNodeExecutor, never()).execute(any(), any());
    verify(userStateService, never()).saveUserState(any(), any());

    ILoggingEvent loggedEvent = logCaptor.events().getFirst();
    assertEquals(Level.WARN, loggedEvent.getLevel());
    assertTrue(loggedEvent.getFormattedMessage().contains(IRRELEVANT_MSG_SENT));
    assertTrue(loggedEvent.getFormattedMessage().contains(USER_INPUT));
    assertTrue(loggedEvent.getFormattedMessage().contains(CHAT_ID));
  }

  @Test
  void navigateMessage_shouldLogWarning_whenCurrentNodeButtonsIsNull() {
    DialogNode currentNodeWithNullButtons = new DialogNode(null, MSG, ButtonType.REPLY, null);

    when(userStateService.getUserStateOrDefault(CHAT_ID, START_NODE_ID)).thenReturn(START_NODE_ID);
    when(dialogStorage.getNode(START_NODE_ID)).thenReturn(currentNodeWithNullButtons);

    dialogNodeNavigator.navigateMessage(CHAT_ID, USER_INPUT);

    verify(dialogNodeExecutor, never()).execute(any(), any());
    verify(userStateService, never()).saveUserState(any(), any());

    ILoggingEvent loggedEvent = logCaptor.events().getFirst();
    assertEquals(Level.WARN, loggedEvent.getLevel());
    assertTrue(loggedEvent.getFormattedMessage().contains(IRRELEVANT_MSG_SENT));
    assertTrue(loggedEvent.getFormattedMessage().contains(USER_INPUT));
    assertTrue(loggedEvent.getFormattedMessage().contains(CHAT_ID));
  }

  @Test
  void navigateCallback_shouldLogWarning_whenNodeDoesNotExist() {
    when(dialogStorage.getNode(CALLBACK_DATA)).thenReturn(null);

    dialogNodeNavigator.navigateCallback(CHAT_ID, CALLBACK_DATA);

    verify(dialogNodeExecutor, never()).execute(any(), any());
    verify(userStateService, never()).saveUserState(any(), any());

    ILoggingEvent loggedEvent = logCaptor.events().getFirst();
    assertEquals(Level.WARN, loggedEvent.getLevel());
    assertTrue(
        loggedEvent
            .getFormattedMessage()
            .contains("Dialog node '" + CALLBACK_DATA + "' not found"));
    assertTrue(loggedEvent.getFormattedMessage().contains(CHAT_ID));
  }

  @Test
  void navigateCallback_shouldExecutesNodeAndSavesState_whenNodeExists() {
    when(dialogStorage.getNode(CALLBACK_DATA)).thenReturn(existingNode);

    dialogNodeNavigator.navigateCallback(CHAT_ID, CALLBACK_DATA);

    verify(dialogNodeExecutor).execute(existingNode, CHAT_ID);
    verify(userStateService).saveUserState(CHAT_ID, CALLBACK_DATA);
  }
}
