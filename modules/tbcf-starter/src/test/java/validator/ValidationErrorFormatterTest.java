package validator;

import com.github.jawisimo.tbcfstarter.validator.ValidationErrorFormatter;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ValidationErrorFormatterTest {

  @Test
  void format_shouldReturnEmptyString_whenErrorsListIsEmpty() {
    String result = ValidationErrorFormatter.format("Header", List.of());

    assertEquals("", result);
  }

  @Test
  void format_shouldReturnSingleErrorWithoutHeader_whenOnlyOneErrorProvided() {
    String error = "Some validation error";

    String result = ValidationErrorFormatter.format("Header", List.of(error));

    assertEquals(error, result);
  }

  @Test
  void format_shouldReturnFormattedMessage_whenMultipleErrorsProvided() {
    String header = "Dialog validation failed";
    List<String> errors = List.of("Node A is missing", "Button B has no target");

    String result = ValidationErrorFormatter.format(header, errors);

    String expected =
        """
                Dialog validation failed with 2 error(s):
                  - Node A is missing
                  - Button B has no target""";

    assertEquals(expected, result);
  }

  @Test
  void format_shouldIncludeCorrectErrorCount_whenMultipleErrorsProvided() {
    String header = "Header";
    List<String> errors = List.of("e1", "e2", "e3");

    String result = ValidationErrorFormatter.format(header, errors);

    assertTrue(result.contains("3 error(s)"));
  }

  @Test
  void format_shouldHandleNullHeader_whenMultipleErrorsProvided() {
    List<String> errors = List.of("e1", "e2");

    String result = ValidationErrorFormatter.format(null, errors);

    assertTrue(result.startsWith("null with 2 error(s):"));
  }
}
