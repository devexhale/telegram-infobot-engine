package interaction.node.model;

import static org.junit.jupiter.api.Assertions.*;

import com.github.jawisimo.tbcfstarter.exception.DialogLoadingException;
import com.github.jawisimo.tbcfstarter.interaction.node.model.ContentNode;
import com.github.jawisimo.tbcfstarter.interaction.node.model.ContentType;
import org.junit.jupiter.api.Test;

class ContentNodeTest {

  @Test
  void constructor_shouldCreateContentNode_whenAllFieldsValid() {
    ContentType type = ContentType.TEXT;
    String text = "Hello";

    ContentNode node = new ContentNode(type, text, null);

    assertEquals(type, node.type());
    assertEquals(text, node.text());
    assertNull(node.media());
  }

  @Test
  void constructor_shouldThrowException_whenTypeIsNull() {
    String text = "Some text";
    String expected = "Content type is missing or not valid";

    DialogLoadingException ex =
        assertThrows(DialogLoadingException.class, () -> new ContentNode(null, text, null));

    assertEquals(expected, ex.getMessage());
  }
}
