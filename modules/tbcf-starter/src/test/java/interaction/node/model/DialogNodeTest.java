package interaction.node.model;

import com.github.jawisimo.tbcfstarter.exception.DialogLoadingException;
import com.github.jawisimo.tbcfstarter.interaction.node.model.Button;
import com.github.jawisimo.tbcfstarter.interaction.node.model.ButtonType;
import com.github.jawisimo.tbcfstarter.interaction.node.model.ContentNode;
import com.github.jawisimo.tbcfstarter.interaction.node.model.DialogNode;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DialogNodeTest {

  @Test
  void constructor_shouldCreateDialogNode_whenAllFieldsValid() {
    List<ContentNode> content = List.of();
    String message = "Hello";
    ButtonType buttonType = ButtonType.REPLY;
    List<Button> buttons = List.of(new Button("Next", "callback", null));

    DialogNode node = new DialogNode(content, message, buttonType, buttons);

    assertEquals(content, node.content());
    assertEquals(message, node.message());
    assertEquals(buttonType, node.buttonType());
    assertEquals(buttons, node.buttons());
  }

  @Test
  void constructor_shouldThrowException_whenMessageIsNull() {
    List<ContentNode> content = List.of();
    ButtonType buttonType = ButtonType.REPLY;
    List<Button> buttons = List.of(new Button("Next", "callback", null));

    String expected = "Field 'message' is missing or blank, but it is required";

    DialogLoadingException ex =
        assertThrows(
            DialogLoadingException.class, () -> new DialogNode(content, null, buttonType, buttons));

    assertEquals(expected, ex.getMessage());
  }

  @Test
  void constructor_shouldThrowException_whenMessageIsBlank() {
    List<ContentNode> content = List.of();
    String message = "   ";
    ButtonType buttonType = ButtonType.REPLY;
    List<Button> buttons = List.of(new Button("Next", "callback", null));

    String expected = "Field 'message' is missing or blank, but it is required";

    DialogLoadingException ex =
        assertThrows(
            DialogLoadingException.class,
            () -> new DialogNode(content, message, buttonType, buttons));

    assertEquals(expected, ex.getMessage());
  }

  @Test
  void constructor_shouldThrowException_whenButtonsIsNull() {
    List<ContentNode> content = List.of();
    String message = "Hello";
    ButtonType buttonType = ButtonType.REPLY;

    String expected = "Buttons list is missing or empty, but it is required";

    DialogLoadingException ex =
        assertThrows(
            DialogLoadingException.class, () -> new DialogNode(content, message, buttonType, null));

    assertEquals(expected, ex.getMessage());
  }

  @Test
  void constructor_shouldThrowException_whenButtonsIsEmpty() {
    List<ContentNode> content = List.of();
    String message = "Hello";
    ButtonType buttonType = ButtonType.REPLY;
    List<Button> buttons = List.of();

    String expected = "Buttons list is missing or empty, but it is required";

    DialogLoadingException ex =
        assertThrows(
            DialogLoadingException.class,
            () -> new DialogNode(content, message, buttonType, buttons));

    assertEquals(expected, ex.getMessage());
  }

  @Test
  void constructor_shouldThrowException_whenMessageAndButtonsInvalid() {
    List<ContentNode> content = List.of();
    String message = "";
    ButtonType buttonType = ButtonType.REPLY;
    List<Button> buttons = List.of();

    String expected =
        """
            Dialog node loading failed with 2 error(s):
              - Field 'message' is missing or blank, but it is required
              - Buttons list is missing or empty, but it is required""";

    DialogLoadingException ex =
        assertThrows(
            DialogLoadingException.class,
            () -> new DialogNode(content, message, buttonType, buttons));

    assertEquals(expected, ex.getMessage());
  }
}
