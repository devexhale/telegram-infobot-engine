package com.github.jawisimo.botengine.validator;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ValidationErrorFormatter {

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
