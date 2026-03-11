package io.github.jawisimo.botsengine.interaction.navigator;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import io.github.jawisimo.botsengine.interaction.navigator.dto.NavigationRequest;
import io.github.jawisimo.botsengine.interaction.navigator.dto.NavigationResult;
import io.github.jawisimo.botsengine.service.UserStateService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;

@ExtendWith(MockitoExtension.class)
class NavigationResultHandlerTest {

  private static final String CHAT_ID = "123";
  private static final String NODE_KEY = "node_1";
  private static final String USER_INPUT = "hello";

  @Mock private UserStateService userStateService;

  @InjectMocks private NavigationResultHandler navigationResultHandler;

  @Test
  void handle_shouldSaveUserState_whenResultIsSuccess() {
    NavigationRequest request = new NavigationRequest(USER_INPUT, NODE_KEY, true);

    navigationResultHandler.handle(CHAT_ID, request, NavigationResult.SUCCESS);

    verify(userStateService).saveUserState(CHAT_ID, NODE_KEY);
  }

  @Test
  void handle_shouldLogError_whenNodeNotFound() {
    NavigationRequest request = new NavigationRequest(USER_INPUT, NODE_KEY, false);
    ListAppender<ILoggingEvent> listAppender = getListAppender();

    navigationResultHandler.handle(CHAT_ID, request, NavigationResult.NODE_NOT_FOUND);

    verifyNoInteractions(userStateService);

    boolean logFound =
        listAppender.list.stream()
            .anyMatch(
                event ->
                    event.getLevel() == Level.WARN
                        && event.getFormattedMessage().contains("Dialog node 'node_1' not found"));

    assertTrue(logFound);
  }

  @Test
  void handle_shouldLogWarning_whenInputIsIrrelevant() {
    NavigationRequest request = new NavigationRequest(USER_INPUT, NODE_KEY, false);
    ListAppender<ILoggingEvent> listAppender = getListAppender();

    navigationResultHandler.handle(CHAT_ID, request, NavigationResult.IRRELEVANT_INPUT);

    verifyNoInteractions(userStateService);

    boolean logFound =
        listAppender.list.stream()
            .anyMatch(
                event ->
                    event.getLevel() == Level.WARN
                        && event
                            .getFormattedMessage()
                            .contains("Irrelevant message sent: 'hello'"));

    assertTrue(logFound);
  }

  private ListAppender<ILoggingEvent> getListAppender() {
    Logger logger = (Logger) LoggerFactory.getLogger(NavigationResultHandler.class);
    ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
    listAppender.start();
    logger.addAppender(listAppender);
    return listAppender;
  }
}
