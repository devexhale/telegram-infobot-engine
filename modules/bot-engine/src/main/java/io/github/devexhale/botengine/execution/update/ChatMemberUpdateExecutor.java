package io.github.devexhale.botengine.execution.update;

import io.github.devexhale.botengine.service.SubscriberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMemberUpdated;

@Component
@RequiredArgsConstructor
public class ChatMemberUpdateExecutor {

  private static final String USER_STATUS_KICKED = "kicked";
  private static final String USER_STATUS_MEMBER = "member";

  private final SubscriberService subscriberService;

  public void execute(ChatMemberUpdated update) {
    String newStatus = update.getNewChatMember().getStatus();
    String chatId = update.getChat().getId().toString();

    if (USER_STATUS_KICKED.equals(newStatus)) {
      subscriberService.unsubscribe(chatId);
    } else if (USER_STATUS_MEMBER.equals(newStatus)) {
      subscriberService.subscribe(chatId);
    }
  }
}
