package io.github.jawisimo.botsengine.loader;

import static org.junit.jupiter.api.Assertions.*;

import io.github.jawisimo.botsengine.exception.DialogLoadingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.InputFile;

class MediaFileLoaderIT {

  private static final String MEDIA_FOLDER = "media";
  private static final String CORRECT_MEDIA_FILE = "Qstart.jpg";

  private MediaFileLoader mediaFileLoader;

  @BeforeEach
  void init() {
    mediaFileLoader = new MediaFileLoader();
  }

  @Test
  void load_shouldReturnInputFile_whenMediaFileExists() {

    InputFile inputFile = mediaFileLoader.load(CORRECT_MEDIA_FILE);

    assertNotNull(inputFile);
    assertEquals(CORRECT_MEDIA_FILE, inputFile.getMediaName());
  }

  @Test
  void load_shouldThrowException_whenMediaFileDoesNotExist() {
    String fileName = "non-existent.mp3";
    String expectedMsg = "Media file not found: '%s/%s'".formatted(MEDIA_FOLDER, fileName);

    DialogLoadingException exception =
        assertThrows(DialogLoadingException.class, () -> mediaFileLoader.load(fileName));

    assertEquals(expectedMsg, exception.getMessage());
  }

  @Test
  void load_shouldPreserveOriginalFileName() {
    InputFile inputFile = mediaFileLoader.load(CORRECT_MEDIA_FILE);

    assertEquals(CORRECT_MEDIA_FILE, inputFile.getMediaName());
  }
}
