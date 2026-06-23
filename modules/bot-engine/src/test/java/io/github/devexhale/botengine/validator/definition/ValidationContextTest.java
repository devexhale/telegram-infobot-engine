package io.github.devexhale.botengine.validator.definition;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ValidationContextTest {

  private static final String ERROR_MSG = "Test error";
  private static final String WARNING_MSG = "Test warning";

  private ValidationContext context;

  @BeforeEach
  void setUp() {
    context = new ValidationContext();
  }

  @Test
  void initialState_shouldHaveEmptyCollectionsAndZeroCounts() {
    assertTrue(context.errors().isEmpty());
    assertTrue(context.warnings().isEmpty());
    assertEquals(0, context.errorCount());
    assertEquals(0, context.warningCount());
    assertFalse(context.hasErrors());
    assertFalse(context.hasWarnings());
  }

  @Test
  void addError_shouldAddErrorAndIncrementCount() {
    context.addError(ERROR_MSG);

    assertEquals(1, context.errorCount());
    assertTrue(context.hasErrors());
    assertEquals(List.of(ERROR_MSG), context.errors());
  }

  @Test
  void addWarning_shouldAddWarningAndIncrementCount() {
    context.addWarning(WARNING_MSG);

    assertEquals(1, context.warningCount());
    assertTrue(context.hasWarnings());
    assertEquals(List.of(WARNING_MSG), context.warnings());
  }

  @Test
  void errorsList_shouldBeUnmodifiable() {
    context.addError(ERROR_MSG);
    List<String> errors = context.errors();

    assertThrows(UnsupportedOperationException.class, () -> errors.add("another error"));
  }

  @Test
  void warningsList_shouldBeUnmodifiable() {
    context.addWarning(WARNING_MSG);
    List<String> warnings = context.warnings();

    assertThrows(UnsupportedOperationException.class, () -> warnings.add("another warning"));
  }
}
