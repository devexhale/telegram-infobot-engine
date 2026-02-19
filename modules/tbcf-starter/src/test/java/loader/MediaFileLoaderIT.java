package loader;

import static org.junit.jupiter.api.Assertions.*;

import com.github.jawisimo.tbcfstarter.exception.DialogLoadingException;
import com.github.jawisimo.tbcfstarter.loader.MediaFileLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.telegram.telegrambots.meta.api.objects.InputFile;

class MediaFileLoaderIT {

  private MediaFileLoader mediaFileLoader;

  private static final String CORRECT_MEDIA_FILE = "photo-test.jpg";

  @BeforeEach
  void init() {
    mediaFileLoader = new MediaFileLoader();
  }

  @Test
  void load_shouldReturnInputFile_whenMediaFileExists() {
    String fileName = "photo-test.jpg";

    InputFile inputFile = mediaFileLoader.load(fileName);

    assertNotNull(inputFile);
    assertEquals(fileName, inputFile.getMediaName());
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {"   ", "\t", "\n"})
  void load_shouldThrowException_whenFileNameIsInvalid(String fileName) {
    String expected = "Media file_name is missing or blank";

    DialogLoadingException exception =
        assertThrows(DialogLoadingException.class, () -> mediaFileLoader.load(fileName));

    assertEquals(expected, exception.getMessage());
  }

  @Test
  void load_shouldThrowException_whenMediaFileDoesNotExist() {
    String fileName = "non-existent.mp3";
    String expected = "Media file not found: " + fileName;

    DialogLoadingException exception =
        assertThrows(DialogLoadingException.class, () -> mediaFileLoader.load(fileName));

    assertEquals(expected, exception.getMessage());
  }

  @Test
  void load_shouldPreserveOriginalFileName() {
    String fileName = "photo-test.jpg";

    InputFile inputFile = mediaFileLoader.load(CORRECT_MEDIA_FILE);

    assertEquals(fileName, inputFile.getMediaName());
  }
}
