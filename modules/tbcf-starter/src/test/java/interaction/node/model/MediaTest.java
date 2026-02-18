package interaction.node.model;

import com.github.jawisimo.tbcfstarter.exception.DialogLoadingException;
import com.github.jawisimo.tbcfstarter.interaction.node.model.Media;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MediaTest {

  @Test
  void constructor_shouldCreateMedia_whenAllFieldsValid() {
    String type = "PHOTO";
    String fileName = "image.jpg";
    String caption = "Some caption";

    Media media = new Media(type, fileName, caption);

    assertEquals(type, media.type());
    assertEquals(fileName, media.fileName());
    assertEquals(caption, media.caption());
  }

  @Test
  void constructor_shouldThrowException_whenTypeIsNull() {
    String fileName = "image.jpg";
    String caption = "Caption";

    String expected = "Media type is missing or blank";

    DialogLoadingException ex =
        assertThrows(DialogLoadingException.class, () -> new Media(null, fileName, caption));

    assertEquals(expected, ex.getMessage());
  }

  @Test
  void constructor_shouldThrowException_whenTypeIsBlank() {
    String type = "  ";
    String fileName = "image.jpg";
    String caption = "Caption";

    String expected = "Media type is missing or blank";

    DialogLoadingException ex =
        assertThrows(DialogLoadingException.class, () -> new Media(type, fileName, caption));

    assertEquals(expected, ex.getMessage());
  }

  @Test
  void constructor_shouldThrowException_whenFileNameIsNull() {
    String type = "PHOTO";
    String caption = "Caption";

    String expected = "Media file name is missing or blank";

    DialogLoadingException ex =
        assertThrows(DialogLoadingException.class, () -> new Media(type, null, caption));

    assertEquals(expected, ex.getMessage());
  }

  @Test
  void constructor_shouldThrowException_whenFileNameIsBlank() {
    String type = "PHOTO";
    String fileName = "   ";
    String caption = "Caption";

    String expected = "Media file name is missing or blank";

    DialogLoadingException ex =
        assertThrows(DialogLoadingException.class, () -> new Media(type, fileName, caption));

    assertEquals(expected, ex.getMessage());
  }

  @Test
  void constructor_shouldThrowException_whenTypeAndFileNameInvalid() {
    String fileName = "  ";
    String caption = "Caption";

    String expected =
        """
                Media loading failed with 2 error(s):
                  - Media type is missing or blank
                  - Media file name is missing or blank""";

    DialogLoadingException ex =
        assertThrows(DialogLoadingException.class, () -> new Media(null, fileName, caption));

    assertEquals(expected, ex.getMessage());
  }
}
