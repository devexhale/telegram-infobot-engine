package io.github.jawisimo.botsengine.interaction.content.handler;

import io.github.jawisimo.botsengine.model.ContentNode;
import io.github.jawisimo.botsengine.model.ContentType;
import io.github.jawisimo.botsengine.model.Media;
import io.github.jawisimo.botsengine.loader.MediaFileLoader;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.generics.TelegramClient;

/**
 * Base {@link ContentHandler} for media-based content nodes.
 *
 * <p>Matches {@link ContentType#MEDIA} nodes by media type and provides helpers for loading media
 * files and accessing media metadata.
 *
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
public abstract class AbstractMediaHandler implements ContentHandler {

  @Getter(AccessLevel.PACKAGE)
  private final TelegramClient client;

  private final MediaFileLoader mediaFileLoader;

  /**
   * Returns {@code true} if this handler supports the media type of the given content node.
   *
   * @param contentNode the content node to evaluate
   * @return {@code true} if the handler can process the node
   */
  @Override
  public boolean canHandle(ContentNode contentNode) {
    if (contentNode.type() != ContentType.MEDIA) {
      return false;
    }
    return contentNode.media().type().equalsIgnoreCase(getMediaType());
  }

  /**
   * Loads a media file from the classpath.
   *
   * @param mediaFileName the media file name under the {@code media} directory
   * @return the loaded {@link InputFile}
   */
  final InputFile getMediaFile(String mediaFileName) {
    return mediaFileLoader.load(mediaFileName);
  }

  /**
   * Returns media metadata from the content node.
   *
   * @param contentNode the content node containing media
   * @return the media metadata
   */
  final Media getMedia(ContentNode contentNode) {
    return contentNode.media();
  }

  /**
   * Returns the media type supported by this handler.
   *
   * @return the supported media type
   */
  abstract String getMediaType();
}
