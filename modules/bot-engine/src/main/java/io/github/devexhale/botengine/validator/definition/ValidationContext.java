package io.github.devexhale.botengine.validator.definition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ValidationContext {

  private final List<String> errors = new ArrayList<>();
  private final List<String> warnings = new ArrayList<>();

  List<String> errors() {
    return Collections.unmodifiableList(errors);
  }

  List<String> warnings() {
    return Collections.unmodifiableList(warnings);
  }

  void addError(String error) {
    errors.add(error);
  }

  void addWarning(String warning) {
    warnings.add(warning);
  }

  boolean hasErrors() {
    return !errors.isEmpty();
  }

  boolean hasWarnings() {
    return !warnings.isEmpty();
  }

  int errorCount() {
    return errors.size();
  }

  int warningCount() {
    return warnings.size();
  }
}
