package com.github.jawisimo.botengine.interaction.content.handler;

import com.github.jawisimo.botengine.interaction.node.model.ContentNode;
import com.github.jawisimo.botengine.interaction.node.model.ContentType;
import com.github.jawisimo.botengine.interaction.node.model.Media;
import com.github.jawisimo.botengine.loader.MediaFileLoader;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
@RequiredArgsConstructor
public abstract class AbstractMediaHandler implements ContentHandler {

  @Getter(AccessLevel.PACKAGE)
  private final TelegramClient client;

  private final MediaFileLoader mediaFileLoader;

  @Override
  public boolean canHandle(ContentNode contentNode) {
    if (contentNode.type() != ContentType.MEDIA) return false;
    return contentNode.media().type().equalsIgnoreCase(getMediaType());
  }

  final InputFile getMediaFile(String mediaFileName) {
    return mediaFileLoader.load(mediaFileName);
  }

  final Media getMedia(ContentNode contentNode) {
    return contentNode.media();
  }

  abstract String getMediaType();
}
