package io.github.devexhale.botengine.loader;

import static org.junit.jupiter.api.Assertions.*;

import io.github.devexhale.botengine.diagnostics.exception.DefinitionInitializationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.telegram.telegrambots.meta.api.objects.InputFile;

@SpringBootTest(classes = {MediaFileLoader.class})
class MediaFileLoaderIT {

  private static final String MEDIA_FOLDER = "content";
  private static final String CORRECT_MEDIA_FILE = "Qstart.jpg";

  @Autowired private MediaFileLoader mediaFileLoader;

  @Test
  void load_shouldReturnInputFile_whenMediaFileExists() {

    InputFile inputFile = mediaFileLoader.load(CORRECT_MEDIA_FILE);

    assertNotNull(inputFile);
    assertEquals(CORRECT_MEDIA_FILE, inputFile.getMediaName());
  }

  @Test
  void load_shouldThrowException_whenMediaFileDoesNotExist() {
    String fileName = "non-existent.mp3";
    String expectedMsg = "Media file '%s/%s' not found".formatted(MEDIA_FOLDER, fileName);

    DefinitionInitializationException exception =
        assertThrows(DefinitionInitializationException.class, () -> mediaFileLoader.load(fileName));

    assertEquals(expectedMsg, exception.getMessage());
  }

  @Test
  void load_shouldPreserveOriginalFileName() {
    InputFile inputFile = mediaFileLoader.load(CORRECT_MEDIA_FILE);

    assertEquals(CORRECT_MEDIA_FILE, inputFile.getMediaName());
  }
}
