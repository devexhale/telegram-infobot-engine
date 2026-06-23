package io.github.devexhale.botengine.execution.update;

import io.github.devexhale.botengine.service.SubscriberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMemberUpdated;

import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatMemberUpdateExecutorTest {

  private static final String CHAT_ID = "123456789";

  @Mock private SubscriberService subscriberService;
  @InjectMocks private ChatMemberUpdateExecutor executor;

  @Mock private ChatMemberUpdated update;
  @Mock private Chat chat;
  @Mock private ChatMember newChatMember;

  @BeforeEach
  void setUp() {
    lenient().when(update.getChat()).thenReturn(chat);
    lenient().when(chat.getId()).thenReturn(Long.valueOf(CHAT_ID));
    lenient().when(update.getNewChatMember()).thenReturn(newChatMember);
  }

  @Test
  void execute_shouldUnsubscribe_whenStatusIsKicked() {
    when(newChatMember.getStatus()).thenReturn("kicked");

    executor.execute(update);

    verify(subscriberService).unsubscribe(CHAT_ID);
    verify(subscriberService, never()).subscribe(CHAT_ID);
  }

  @Test
  void execute_shouldSubscribe_whenStatusIsMember() {
    when(newChatMember.getStatus()).thenReturn("member");

    executor.execute(update);

    verify(subscriberService).subscribe(CHAT_ID);
    verify(subscriberService, never()).unsubscribe(CHAT_ID);
  }

  @Test
  void execute_shouldDoNothing_whenStatusIsOther() {
    when(newChatMember.getStatus()).thenReturn("administrator");

    executor.execute(update);

    verify(subscriberService, never()).subscribe(CHAT_ID);
    verify(subscriberService, never()).unsubscribe(CHAT_ID);
  }
}
