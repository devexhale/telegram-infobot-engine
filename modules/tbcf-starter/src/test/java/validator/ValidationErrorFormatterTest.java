package validator;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.jawisimo.tbcfstarter.validator.ValidationErrorFormatter;
import java.util.List;
import org.junit.jupiter.api.Test;

class ValidationErrorFormatterTest {

  @Test
  void format_shouldReturnEmptyString_whenErrorsListIsEmpty() {
    String result = ValidationErrorFormatter.format("Header", List.of());

    assertThat(result).isEmpty();
  }

  @Test
  void format_shouldReturnSingleErrorWithoutHeader_whenOnlyOneErrorProvided() {
    String error = "Some validation error";

    String result = ValidationErrorFormatter.format("Header", List.of(error));

    assertThat(result).isEqualTo(error);
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

    assertThat(result).isEqualTo(expected);
  }

  @Test
  void format_shouldIncludeCorrectErrorCount_whenMultipleErrorsProvided() {
    String header = "Header";
    List<String> errors = List.of("e1", "e2", "e3");

    String result = ValidationErrorFormatter.format(header, errors);

    assertThat(result).contains("3 error(s)");
  }

  @Test
  void format_shouldHandleNullHeader_whenMultipleErrorsProvided() {
    List<String> errors = List.of("e1", "e2");

    String result = ValidationErrorFormatter.format(null, errors);

    assertThat(result).startsWith("null with 2 error(s):");
  }
}
