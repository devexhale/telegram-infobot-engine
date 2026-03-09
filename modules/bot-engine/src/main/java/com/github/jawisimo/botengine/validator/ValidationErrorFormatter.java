package com.github.jawisimo.botengine.validator;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.experimental.UtilityClass;

/**
 * Utility for formatting validation errors and warnings.
 *
 * <p>Builds a numbered list of issues with a header message.
 *
 * <p>Each issue is prefixed with its index starting from 1.
 *
 * <p>The formatted output is used in validation exceptions and logs.
 *
 * @since 1.0
 */
@UtilityClass
public class ValidationErrorFormatter {

  /**
   * Formats a list of issues with a header and numbered entries.
   *
   * <p>Each issue is placed on a new line and prefixed with its index.
   *
   * @param header the header message describing the validation context
   * @param issues the list of validation issues
   * @return formatted validation message
   */
  public static String format(String header, List<String> issues) {
    String formattedIssues =
        IntStream.range(0, issues.size())
            .mapToObj(i -> (i + 1) + ". " + issues.get(i))
            .collect(Collectors.joining(System.lineSeparator()));

    return header + System.lineSeparator() + formattedIssues;
  }
}
