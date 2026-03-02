package com.github.jawisimo.botengine.loader;

import com.github.jawisimo.botengine.exception.DialogLoadingException;
import java.io.InputStream;
import java.nio.file.Paths;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.InputFile;

/**
 * Loads media files from the classpath and wraps them as {@link InputFile}.
 *
 * <p>Resolves media resources from the {@code media} directory and prepares them for sending via
 * the Telegram API.
 *
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MediaFileLoader {

  private static final String MEDIA_FOLDER = "media";

  /**
   * Loads a media file from the classpath.
   *
   * @param mediaFileName the media file name located under the {@code media} directory
   * @return the loaded {@link InputFile}
   * @throws DialogLoadingException if the file name is invalid or the resource cannot be found
   */
  public InputFile load(String mediaFileName) {
    if (mediaFileName == null || mediaFileName.isBlank()) {
      throw new DialogLoadingException("Media file_name is missing or blank");
    }

    String resourcePath = Paths.get(MEDIA_FOLDER, mediaFileName).toString();
    InputStream is = getClass().getClassLoader().getResourceAsStream(resourcePath);

    if (is == null) {
      throw new DialogLoadingException("Media file not found: " + mediaFileName);
    }

    return new InputFile(is, mediaFileName);
  }
}
