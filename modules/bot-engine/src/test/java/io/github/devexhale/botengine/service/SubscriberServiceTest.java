package io.github.devexhale.botengine.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import io.github.devexhale.botengine.repository.message.MessageCleanupRepository;
import io.github.devexhale.botengine.repository.subscribe.SubscriberRepository;
import io.github.devexhale.botengine.testsupport.logger.TestLogCaptor;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SubscriberServiceTest {

  private static final String DEFAULT_CHAT_ID = "123456789";
  private static final String CHAT_ID_1 = "111111111";
  private static final String CHAT_ID_2 = "222222222";

  @Mock private SubscriberRepository subscriberRepository;
  @Mock private UserStateService userStateService;
  @Mock private MessageCleanupRepository messageCleanupRepository;

  @InjectMocks private SubscriberService subscriberService;

  private TestLogCaptor logCaptor;

  @BeforeEach
  void setUp() {
    logCaptor = new TestLogCaptor(SubscriberService.class);
  }

  @AfterEach
  void tearDown() {
    logCaptor.close();
  }

  @Test
  void subscribe_shouldAddToRepositoryAndLogInfo_whenCalled() {
    subscriberService.subscribe(DEFAULT_CHAT_ID);

    verify(subscriberRepository).save(DEFAULT_CHAT_ID);

    ILoggingEvent loggedEvent = logCaptor.events().getFirst();
    assertEquals(Level.INFO, loggedEvent.getLevel());
    assertTrue(loggedEvent.getFormattedMessage().contains("User subscribed"));
    assertTrue(loggedEvent.getFormattedMessage().contains(DEFAULT_CHAT_ID));
  }

  @Test
  void unsubscribe_shouldDeleteFromAllReposAndLogInfo_whenCalled() {
    subscriberService.unsubscribe(DEFAULT_CHAT_ID);

    verify(subscriberRepository).delete(DEFAULT_CHAT_ID);
    verify(userStateService).deleteUserState(DEFAULT_CHAT_ID);
    verify(messageCleanupRepository).deleteAllByChatId(DEFAULT_CHAT_ID);

    ILoggingEvent loggedEvent = logCaptor.events().getFirst();
    assertEquals(Level.INFO, loggedEvent.getLevel());
    assertTrue(loggedEvent.getFormattedMessage().contains("User unsubscribed"));
    assertTrue(loggedEvent.getFormattedMessage().contains(DEFAULT_CHAT_ID));
  }

  @Test
  void getAllSubscribers_shouldReturnRepositoryResult_whenCalled() {
    Set<String> expectedSubscribers = Set.of(CHAT_ID_1, CHAT_ID_2);
    when(subscriberRepository.getAll()).thenReturn(expectedSubscribers);

    Set<String> result = subscriberService.getAllSubscribers();

    assertEquals(expectedSubscribers, result);
  }
}
