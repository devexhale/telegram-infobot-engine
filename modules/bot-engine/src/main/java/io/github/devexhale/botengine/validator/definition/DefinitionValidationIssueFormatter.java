package io.github.devexhale.botengine.validator.definition;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.experimental.UtilityClass;

/**
 * Utility for formatting validation errors and warnings into a numbered list.
 *
 * @since 1.0
 */
@UtilityClass
public class DefinitionValidationIssueFormatter {

  /**
   * Formats a list of issues with a header and numbered entries.
   *
   * @param header the header message describing the validation context
   * @param issues the list of validation issues
   * @return the formatted validation message
   */
  public static String format(String header, List<String> issues) {
    String formattedIssues =
        IntStream.range(0, issues.size())
            .mapToObj(i -> (i + 1) + ". " + issues.get(i))
            .collect(Collectors.joining(System.lineSeparator()));

    return header + System.lineSeparator() + formattedIssues;
  }
}
