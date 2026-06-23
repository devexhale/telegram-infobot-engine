package io.github.devexhale.botengine.execution.common.content.handler;

import io.github.devexhale.botengine.diagnostics.exception.TelegramMessageSendException;
import io.github.devexhale.botengine.domain.content.ContentNode;
import io.github.devexhale.botengine.domain.content.ContentType;
import io.github.devexhale.botengine.loader.MediaFileLoader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
@RequiredArgsConstructor
@Slf4j
public class PhotoContentHandler implements ContentHandler {

  private final TelegramClient client;
  private final MediaFileLoader mediaFileLoader;

  @Override
  public ContentType type() {
    return ContentType.PHOTO;
  }

  @Override
  public Message handle(ContentNode node, String chatId) {
    SendPhoto request = createRequest(node, chatId);

    try {
      return client.execute(request);
    } catch (TelegramApiException e) {
      log.error("Failed to send photo in chat. ChatID={}", chatId);
      throw new TelegramMessageSendException(e);
    }
  }

  private SendPhoto createRequest(ContentNode node, String chatId) {
    return SendPhoto.builder()
        .chatId(chatId)
        .photo(mediaFileLoader.load(node.fileName()))
        .caption(node.caption())
        .build();
  }
}
