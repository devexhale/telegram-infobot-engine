package com.github.jawisimo.botengine.validator;

import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Utility class for formatting validation error messages.
 *
 * <p>Produces human-readable error descriptions used during dialog validation.
 *
 * @since 1.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ValidationErrorFormatter {

  /**
   * Formats validation errors into a readable message.
   *
   * @param header the message header describing the validation context
   * @param errors the list of validation errors
   * @return a formatted error message or an empty string if no errors exist
   */
  public static String format(String header, List<String> errors) {
    if (errors.isEmpty()) {
      return "";
    }

    if (errors.size() == 1) {
      return errors.getFirst();
    }

    return header
        + " with "
        + errors.size()
        + " error(s):\n"
        + errors.stream().map(e -> "  - " + e).collect(Collectors.joining("\n"));
  }
}
