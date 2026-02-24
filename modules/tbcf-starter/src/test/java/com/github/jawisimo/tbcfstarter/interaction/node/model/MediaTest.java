package com.github.jawisimo.tbcfstarter.interaction.node.model;

import com.github.jawisimo.tbcfstarter.exception.DialogLoadingException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MediaTest {

  private static final String MEDIA_TYPE = "photo";
  private static final String FILE_NAME = "image.jpg";
  private static final String CAPTION = "Some caption...";
  private static final String MEDIA_TYPE_MISSING_MSG = "Media type is missing or blank";
  private static final String MEDIA_FILE_NAME_MISSING_MSG = "Media file name is missing or blank";

  @Test
  void constructor_shouldCreateMedia_whenAllFieldsValid() {
    Media media = new Media(MEDIA_TYPE, FILE_NAME, CAPTION);

    assertEquals(MEDIA_TYPE, media.type());
    assertEquals(FILE_NAME, media.fileName());
    assertEquals(CAPTION, media.caption());
  }

  @Test
  void constructor_shouldThrowException_whenTypeIsNull() {

    DialogLoadingException ex =
        assertThrows(DialogLoadingException.class, () -> new Media(null, FILE_NAME, CAPTION));

    assertEquals(MEDIA_TYPE_MISSING_MSG, ex.getMessage());
  }

  @Test
  void constructor_shouldThrowException_whenTypeIsBlank() {
    String type = " ";

    DialogLoadingException ex =
        assertThrows(DialogLoadingException.class, () -> new Media(type, MEDIA_TYPE, CAPTION));

    assertEquals(MEDIA_TYPE_MISSING_MSG, ex.getMessage());
  }

  @Test
  void constructor_shouldThrowException_whenFileNameIsNull() {
    String type = "PHOTO";

    DialogLoadingException ex =
        assertThrows(DialogLoadingException.class, () -> new Media(type, null, CAPTION));

    assertEquals(MEDIA_FILE_NAME_MISSING_MSG, ex.getMessage());
  }

  @Test
  void constructor_shouldThrowException_whenFileNameIsBlank() {
    String fileName = "   ";

    DialogLoadingException ex =
        assertThrows(
            DialogLoadingException.class,
            () -> new Media(MEDIA_TYPE_MISSING_MSG, fileName, CAPTION));

    assertEquals(MEDIA_FILE_NAME_MISSING_MSG, ex.getMessage());
  }

  @Test
  void constructor_shouldThrowException_whenTypeAndFileNameInvalid() {
    String fileName = "  ";

    String expected =
        """
                Media loading failed with 2 error(s):
                  - Media type is missing or blank
                  - Media file name is missing or blank""";

    DialogLoadingException ex =
        assertThrows(DialogLoadingException.class, () -> new Media(null, fileName, CAPTION));

    assertEquals(expected, ex.getMessage());
  }
}
