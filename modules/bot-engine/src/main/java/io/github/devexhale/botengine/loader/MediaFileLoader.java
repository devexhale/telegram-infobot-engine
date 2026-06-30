package io.github.devexhale.botengine.loader;

import io.github.devexhale.botengine.diagnostics.exception.DefinitionInitializationException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.InputFile;

/**
 * Loads media files from the classpath and wraps them as {@link InputFile}.
 *
 * <p>Resolves media resources from the {@code content} directory and prepares them for sending via
 * the Telegram API.
 *
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MediaFileLoader {

  private static final String MEDIA_FOLDER = "content";

  private final ResourceLoader resourceLoader;

  /**
   * Loads a media file from the classpath.
   *
   * @param mediaFileName the file name located under the {@code content} directory
   * @return the loaded {@link InputFile}
   * @throws DefinitionInitializationException if the resource cannot be found or read
   */
  public InputFile load(String mediaFileName) {
    String resourcePath = Paths.get(MEDIA_FOLDER, mediaFileName).toString();
    Resource resource = resourceLoader.getResource(resourcePath);

    if (!resource.exists()) {
      throw new DefinitionInitializationException(
          "Media file '%s/%s' not found".formatted(MEDIA_FOLDER, mediaFileName));
    }

    try {
      InputStream is = resource.getInputStream();
      return new InputFile(is, mediaFileName);
    } catch (IOException e) {
      throw new DefinitionInitializationException(
          "Cannot read media file '%s/%s'".formatted(MEDIA_FOLDER, mediaFileName), e);
    }
  }
}
