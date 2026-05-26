package io.github.devexhale.botengine.validator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import io.github.devexhale.botengine.validator.definition.util.DefinitionValidationIssueFormatter;
import org.junit.jupiter.api.Test;

class DefinitionValidationIssueFormatterTest {

  private static final String HEADER = "Validation failed";
  private static final String ISSUE_ONE = "First issue";
  private static final String ISSUE_TWO = "Second issue";
  private static final String ISSUE_THREE = "Third issue";
  private static final String NL = System.lineSeparator();

  @Test
  void format_shouldReturnHeaderAndNumberedIssues_whenMultipleIssuesProvided() {
    List<String> issues = List.of(ISSUE_ONE, ISSUE_TWO, ISSUE_THREE);

    String result = DefinitionValidationIssueFormatter.format(HEADER, issues);

    String expected =
        HEADER + NL + "1. " + ISSUE_ONE + NL + "2. " + ISSUE_TWO + NL + "3. " + ISSUE_THREE;

    assertEquals(expected, result);
  }

  @Test
  void format_shouldReturnHeaderAndSingleIssue_whenOneIssueProvided() {
    List<String> issues = List.of(ISSUE_ONE);

    String result = DefinitionValidationIssueFormatter.format(HEADER, issues);

    String expected = HEADER + NL + "1. " + ISSUE_ONE;

    assertEquals(expected, result);
  }

  @Test
  void format_shouldReturnHeaderAndNoIssues_whenIssueListIsEmpty() {
    List<String> issues = List.of();

    String result = DefinitionValidationIssueFormatter.format(HEADER, issues);

    String expected = HEADER + NL;

    assertEquals(expected, result);
  }
}
