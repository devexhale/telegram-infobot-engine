package loader;

import static org.junit.jupiter.api.Assertions.*;

import com.github.jawisimo.tbcfstarter.exception.DialogLoadingException;
import com.github.jawisimo.tbcfstarter.interaction.node.model.DialogMap;
import com.github.jawisimo.tbcfstarter.interaction.node.model.DialogNode;
import com.github.jawisimo.tbcfstarter.loader.DialogLoader;
import com.github.jawisimo.tbcfstarter.parser.DialogParser;
import com.github.jawisimo.tbcfstarter.parser.DialogParserProvider;
import com.github.jawisimo.tbcfstarter.parser.JsonDialogParser;
import com.github.jawisimo.tbcfstarter.parser.YamlDialogParser;
import com.github.jawisimo.tbcfstarter.validator.DialogValidator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
    classes = {
      DialogLoader.class,
      DialogValidator.class,
      DialogParser.class,
      JsonDialogParser.class,
      YamlDialogParser.class,
      DialogParserProvider.class
    })
class DialogLoaderIT {

  @Autowired private DialogLoader dialogLoader;

  private static final String FAIL_LOAD_MESSAGE_PREFIX = "Failed to load dialog file: ";

  @ParameterizedTest
  @ValueSource(strings = {"dialog-test.yml", "dialog-test.json"})
  void load_shouldSuccessfullyLoadValidDialog(String fileName) {
    String startNodeId = "/start";
    String message = "Почнемо нашу подорож. Оберіть тему: ";

    DialogMap dialogMap = dialogLoader.load(fileName);
    DialogNode startNode = dialogMap.getNode(startNodeId);

    assertNotNull(dialogMap);
    assertTrue(dialogMap.containsNodeKey(startNodeId));
    assertEquals(message, startNode.message());
    assertNotNull(startNode.buttons());
    assertNotNull(startNode.content());
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "incorrect-syntax-dialog.yml",
        "incorrect-syntax-dialog.json",
        "incorrect-structure-dialog.yml",
        "incorrect-structure-dialog.json"
      })
  void load_shouldThrowException_whenDialogFileIsInvalid(String fileName) {
    String expected = FAIL_LOAD_MESSAGE_PREFIX + fileName;

    DialogLoadingException exception =
        assertThrows(DialogLoadingException.class, () -> dialogLoader.load(fileName));

    assertEquals(expected, exception.getMessage());
    assertNotNull(exception.getCause());
  }

  @ParameterizedTest
  @ValueSource(strings = {"not-exist.yml", "not-exist.json"})
  void load_shouldThrowException_whenFileDoesNotExist(String fileName) {
    String expected = FAIL_LOAD_MESSAGE_PREFIX + fileName;

    DialogLoadingException exception =
        assertThrows(DialogLoadingException.class, () -> dialogLoader.load(fileName));

    assertEquals(expected, exception.getMessage());
  }
}
