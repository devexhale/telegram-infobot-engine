package validator;

import com.github.jawisimo.tbcfstarter.exception.DialogLoadingException;
import com.github.jawisimo.tbcfstarter.interaction.command.commandset.StartCommand;
import com.github.jawisimo.tbcfstarter.interaction.media.handler.MediaHandler;
import com.github.jawisimo.tbcfstarter.interaction.node.model.*;
import com.github.jawisimo.tbcfstarter.validator.DialogValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DialogValidatorTest {

  private DialogValidator validator;

  @Mock private DialogMap dialogMap;

  @Mock private MediaHandler mediaHandler;

  @BeforeEach
  void init() {
    validator = new DialogValidator();
  }

  @Test
  void validateStartNode_shouldNotThrow_whenStartNodeExists() {
    when(dialogMap.containsNodeKey(StartCommand.COMMAND_NAME)).thenReturn(true);

    assertDoesNotThrow(() -> validator.validateStartNode(dialogMap, "file.yml"));
  }

  @Test
  void validateStartNode_shouldThrowException_whenStartNodeMissing() {
    when(dialogMap.containsNodeKey(StartCommand.COMMAND_NAME)).thenReturn(false);

    DialogLoadingException ex =
        assertThrows(
            DialogLoadingException.class, () -> validator.validateStartNode(dialogMap, "file.yml"));

    assertTrue(ex.getMessage().contains("/start"));
  }

  @Test
  void validateContent_shouldThrowException_whenTextTypeAndTextIsNull() {
    ContentNode node = mock(ContentNode.class);
    when(node.type()).thenReturn(ContentType.TEXT);
    when(node.text()).thenReturn(null);

    List<MediaHandler> handlers = List.of();

    assertThrows(DialogLoadingException.class, () -> validator.validateContent(node, handlers));
  }

  @Test
  void validateContent_shouldThrowException_whenTextTypeAndTextIsEmpty() {
    ContentNode node = mock(ContentNode.class);
    when(node.type()).thenReturn(ContentType.TEXT);
    when(node.text()).thenReturn("");

    List<MediaHandler> handlers = List.of();

    assertThrows(DialogLoadingException.class, () -> validator.validateContent(node, handlers));
  }

  @Test
  void validateContent_shouldNotThrow_whenTextTypeAndTextIsValid() {
    ContentNode node = mock(ContentNode.class);
    when(node.type()).thenReturn(ContentType.TEXT);
    when(node.text()).thenReturn("Hello");

    assertDoesNotThrow(() -> validator.validateContent(node, List.of()));
  }

  @Test
  void validateContent_shouldNotThrow_whenMediaIsNull() {
    ContentNode node = mock(ContentNode.class);
    when(node.type()).thenReturn(ContentType.TEXT);
    when(node.text()).thenReturn("Hello");
    when(node.media()).thenReturn(null);

    assertDoesNotThrow(() -> validator.validateContent(node, List.of()));
  }

  @Test
  void validateContent_shouldThrowException_whenNoMediaHandlerSupports() {
    ContentNode node = mock(ContentNode.class);
    Media media = mock(Media.class);

    when(node.media()).thenReturn(media);
    when(media.type()).thenReturn("PHOTO");
    when(media.fileName()).thenReturn("image.jpg");
    when(mediaHandler.canHandle(node)).thenReturn(false);

    List<MediaHandler> handlers = List.of(mediaHandler);

    DialogLoadingException ex =
        assertThrows(DialogLoadingException.class, () -> validator.validateContent(node, handlers));

    assertTrue(ex.getMessage().contains("No handler found"));
  }

  @Test
  void validateContent_shouldNotThrow_whenMediaHandlerSupports() {
    ContentNode node = mock(ContentNode.class);
    Media media = mock(Media.class);

    when(node.media()).thenReturn(media);
    when(mediaHandler.canHandle(node)).thenReturn(true);

    assertDoesNotThrow(() -> validator.validateContent(node, List.of(mediaHandler)));
  }

  @Test
  void validateButtons_shouldThrowException_whenReplyButtonHasUrl() {
    DialogNode node = mock(DialogNode.class);
    Button button = mock(Button.class);

    when(node.buttonType()).thenReturn(ButtonType.REPLY);
    when(node.buttons()).thenReturn(List.of(button));
    when(button.url()).thenReturn("http://test");
    when(button.next()).thenReturn("next");

    assertThrows(DialogLoadingException.class, () -> validator.validateButtons(node));
  }

  @Test
  void validateButtons_shouldThrowException_whenReplyButtonHasNoNext() {
    DialogNode node = mock(DialogNode.class);
    Button button = mock(Button.class);

    when(node.buttonType()).thenReturn(ButtonType.REPLY);
    when(node.buttons()).thenReturn(List.of(button));
    when(button.url()).thenReturn(null);
    when(button.next()).thenReturn(null);

    assertThrows(DialogLoadingException.class, () -> validator.validateButtons(node));
  }

  @Test
  void validateButtons_shouldNotThrow_whenReplyButtonIsValid() {
    DialogNode node = mock(DialogNode.class);
    Button button = mock(Button.class);

    when(node.buttonType()).thenReturn(ButtonType.REPLY);
    when(node.buttons()).thenReturn(List.of(button));
    when(button.url()).thenReturn(null);
    when(button.next()).thenReturn("next");

    assertDoesNotThrow(() -> validator.validateButtons(node));
  }

  @Test
  void validateButtons_shouldThrowException_whenInlineButtonHasNeitherUrlNorNext() {
    DialogNode node = mock(DialogNode.class);
    Button button = mock(Button.class);

    when(node.buttonType()).thenReturn(ButtonType.INLINE);
    when(node.buttons()).thenReturn(List.of(button));
    when(button.url()).thenReturn(null);
    when(button.next()).thenReturn(null);

    assertThrows(DialogLoadingException.class, () -> validator.validateButtons(node));
  }

  @Test
  void validateButtons_shouldThrowException_whenInlineButtonHasBothUrlAndNext() {
    DialogNode node = mock(DialogNode.class);
    Button button = mock(Button.class);

    when(node.buttonType()).thenReturn(ButtonType.INLINE);
    when(node.buttons()).thenReturn(List.of(button));
    when(button.url()).thenReturn("http://test");
    when(button.next()).thenReturn("next");

    assertThrows(DialogLoadingException.class, () -> validator.validateButtons(node));
  }

  @Test
  void validateButtons_shouldNotThrow_whenInlineButtonHasOnlyUrl() {
    DialogNode node = mock(DialogNode.class);
    Button button = mock(Button.class);

    when(node.buttonType()).thenReturn(ButtonType.INLINE);
    when(node.buttons()).thenReturn(List.of(button));
    when(button.url()).thenReturn("http://test");
    when(button.next()).thenReturn(null);

    assertDoesNotThrow(() -> validator.validateButtons(node));
  }

  @Test
  void validateButtons_shouldNotThrow_whenInlineButtonHasOnlyNext() {
    DialogNode node = mock(DialogNode.class);
    Button button = mock(Button.class);

    when(node.buttonType()).thenReturn(ButtonType.INLINE);
    when(node.buttons()).thenReturn(List.of(button));
    when(button.url()).thenReturn(null);
    when(button.next()).thenReturn("next");

    assertDoesNotThrow(() -> validator.validateButtons(node));
  }
}
