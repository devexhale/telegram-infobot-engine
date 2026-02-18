package interaction.node.model;

import com.github.jawisimo.tbcfstarter.exception.DialogLoadingException;
import com.github.jawisimo.tbcfstarter.interaction.node.model.Button;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ButtonTest {

  @Test
  void constructor_shouldCreateButton_whenLabelIsValid() {
    String label = "Click me";
    String next = "nextNode";

    Button button = new Button(label, next, null);

    assertEquals(label, button.label());
    assertEquals(next, button.next());
  }

  @Test
  void constructor_shouldThrowException_whenLabelIsNull() {
    DialogLoadingException exception =
        assertThrows(DialogLoadingException.class, () -> new Button(null, "next", null));

    assertEquals("Button label is missing or blank", exception.getMessage());
  }

  @Test
  void constructor_shouldThrowException_whenLabelIsBlank() {
    DialogLoadingException exception =
        assertThrows(DialogLoadingException.class, () -> new Button("   ", null, "url"));

    String expected = "Button label is missing or blank";

    assertEquals(expected, exception.getMessage());
  }
}
