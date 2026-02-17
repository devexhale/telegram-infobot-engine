package validator;

import com.jawisimo.tbcfstarter.exception.DialogLoadingException;
import com.jawisimo.tbcfstarter.interaction.command.commandset.StartCommand;
import com.jawisimo.tbcfstarter.interaction.media.handler.MediaHandler;
import com.jawisimo.tbcfstarter.interaction.node.model.*;
import com.jawisimo.tbcfstarter.validator.DialogValidator;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
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

    assertThatCode(() -> validator.validateStartNode(dialogMap, "file.yml"))
        .doesNotThrowAnyException();
  }

  @Test
  void validateStartNode_shouldThrowException_whenStartNodeMissing() {
    when(dialogMap.containsNodeKey(StartCommand.COMMAND_NAME)).thenReturn(false);

    assertThatThrownBy(() -> validator.validateStartNode(dialogMap, "file.yml"))
        .isInstanceOf(DialogLoadingException.class)
        .hasMessageContaining("/start");
  }

  @Test
  void validateContent_shouldThrowException_whenTextTypeAndTextIsNull() {
    ContentNode node = mock(ContentNode.class);
    when(node.type()).thenReturn(ContentType.TEXT);
    when(node.text()).thenReturn(null);

    ThrowableAssert.ThrowingCallable action = () -> validator.validateContent(node, List.of());

    assertThatThrownBy(action).isInstanceOf(DialogLoadingException.class);
  }

  @Test
  void validateContent_shouldThrowException_whenTextTypeAndTextIsEmpty() {
    ContentNode node = mock(ContentNode.class);
    when(node.type()).thenReturn(ContentType.TEXT);
    when(node.text()).thenReturn("");

    ThrowableAssert.ThrowingCallable action = () -> validator.validateContent(node, List.of());

    assertThatThrownBy(action).isInstanceOf(DialogLoadingException.class);
  }

  @Test
  void validateContent_shouldNotThrow_whenTextTypeAndTextIsValid() {
    ContentNode node = mock(ContentNode.class);
    when(node.type()).thenReturn(ContentType.TEXT);
    when(node.text()).thenReturn("Hello");

    assertThatCode(() -> validator.validateContent(node, List.of())).doesNotThrowAnyException();
  }

  @Test
  void validateContent_shouldNotThrow_whenMediaIsNull() {
    ContentNode node = mock(ContentNode.class);
    when(node.type()).thenReturn(ContentType.TEXT);
    when(node.text()).thenReturn("Hello");
    when(node.media()).thenReturn(null);

    assertThatCode(() -> validator.validateContent(node, List.of())).doesNotThrowAnyException();
  }

  @Test
  void validateContent_shouldThrowException_whenNoMediaHandlerSupports() {
    ContentNode node = mock(ContentNode.class);
    Media media = mock(Media.class);

    when(node.media()).thenReturn(media);
    when(media.type()).thenReturn("PHOTO");
    when(media.fileName()).thenReturn("image.jpg");
    when(mediaHandler.canHandle(node)).thenReturn(false);

    ThrowableAssert.ThrowingCallable action =
        () -> validator.validateContent(node, List.of(mediaHandler));

    assertThatThrownBy(action)
        .isInstanceOf(DialogLoadingException.class)
        .hasMessageContaining("No handler found");
  }

  @Test
  void validateContent_shouldNotThrow_whenMediaHandlerSupports() {
    ContentNode node = mock(ContentNode.class);
    Media media = mock(Media.class);

    when(node.media()).thenReturn(media);
    when(mediaHandler.canHandle(node)).thenReturn(true);

    assertThatCode(() -> validator.validateContent(node, List.of(mediaHandler)))
        .doesNotThrowAnyException();
  }

  @Test
  void validateButtons_shouldThrowException_whenReplyButtonHasUrl() {
    DialogNode node = mock(DialogNode.class);
    Button button = mock(Button.class);

    when(node.buttonType()).thenReturn(ButtonType.REPLY);
    when(node.buttons()).thenReturn(List.of(button));
    when(button.url()).thenReturn("http://test");
    when(button.next()).thenReturn("next");

    assertThatThrownBy(() -> validator.validateButtons(node))
        .isInstanceOf(DialogLoadingException.class);
  }

  @Test
  void validateButtons_shouldThrowException_whenReplyButtonHasNoNext() {
    DialogNode node = mock(DialogNode.class);
    Button button = mock(Button.class);

    when(node.buttonType()).thenReturn(ButtonType.REPLY);
    when(node.buttons()).thenReturn(List.of(button));
    when(button.url()).thenReturn(null);
    when(button.next()).thenReturn(null);

    assertThatThrownBy(() -> validator.validateButtons(node))
        .isInstanceOf(DialogLoadingException.class);
  }

  @Test
  void validateButtons_shouldNotThrow_whenReplyButtonIsValid() {
    DialogNode node = mock(DialogNode.class);
    Button button = mock(Button.class);

    when(node.buttonType()).thenReturn(ButtonType.REPLY);
    when(node.buttons()).thenReturn(List.of(button));
    when(button.url()).thenReturn(null);
    when(button.next()).thenReturn("next");

    assertThatCode(() -> validator.validateButtons(node)).doesNotThrowAnyException();
  }

  @Test
  void validateButtons_shouldThrowException_whenInlineButtonHasNeitherUrlNorNext() {
    DialogNode node = mock(DialogNode.class);
    Button button = mock(Button.class);

    when(node.buttonType()).thenReturn(ButtonType.INLINE);
    when(node.buttons()).thenReturn(List.of(button));
    when(button.url()).thenReturn(null);
    when(button.next()).thenReturn(null);

    assertThatThrownBy(() -> validator.validateButtons(node))
        .isInstanceOf(DialogLoadingException.class);
  }

  @Test
  void validateButtons_shouldThrowException_whenInlineButtonHasBothUrlAndNext() {
    DialogNode node = mock(DialogNode.class);
    Button button = mock(Button.class);

    when(node.buttonType()).thenReturn(ButtonType.INLINE);
    when(node.buttons()).thenReturn(List.of(button));
    when(button.url()).thenReturn("http://test");
    when(button.next()).thenReturn("next");

    assertThatThrownBy(() -> validator.validateButtons(node))
        .isInstanceOf(DialogLoadingException.class);
  }

  @Test
  void validateButtons_shouldNotThrow_whenInlineButtonHasOnlyUrl() {
    DialogNode node = mock(DialogNode.class);
    Button button = mock(Button.class);

    when(node.buttonType()).thenReturn(ButtonType.INLINE);
    when(node.buttons()).thenReturn(List.of(button));
    when(button.url()).thenReturn("http://test");
    when(button.next()).thenReturn(null);

    assertThatCode(() -> validator.validateButtons(node)).doesNotThrowAnyException();
  }

  @Test
  void validateButtons_shouldNotThrow_whenInlineButtonHasOnlyNext() {
    DialogNode node = mock(DialogNode.class);
    Button button = mock(Button.class);

    when(node.buttonType()).thenReturn(ButtonType.INLINE);
    when(node.buttons()).thenReturn(List.of(button));
    when(button.url()).thenReturn(null);
    when(button.next()).thenReturn("next");

    assertThatCode(() -> validator.validateButtons(node)).doesNotThrowAnyException();
  }
}
