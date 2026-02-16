package com.jawisimo.tbcfstarter.loader;

import com.jawisimo.tbcfstarter.exception.DialogLoadingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.InputStream;
import java.nio.file.Paths;

@Component
@RequiredArgsConstructor
@Slf4j
public class MediaFileLoader {
  private static final String MEDIA_FOLDER = "media";

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
