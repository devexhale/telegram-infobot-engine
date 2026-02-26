package com.github.jawisimo.botengine.interaction.node.model;

import com.github.jawisimo.botengine.exception.DialogLoadingException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ButtonTest {

  private static final String BUTON_LABEL_MISSING_MSG = "Button label is missing or blank";

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

    assertEquals(BUTON_LABEL_MISSING_MSG, exception.getMessage());
  }

  @Test
  void constructor_shouldThrowException_whenLabelIsBlank() {
    DialogLoadingException exception =
        assertThrows(DialogLoadingException.class, () -> new Button("   ", null, "url"));

    assertEquals(BUTON_LABEL_MISSING_MSG, exception.getMessage());
  }
}
