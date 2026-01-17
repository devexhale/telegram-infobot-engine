package com.jawisimo.tbcfstarter.validator;

import java.util.List;
import java.util.stream.Collectors;

public class ValidationErrorFormatter {

    public static String format(String header, List<String> errors) {
        if (errors.isEmpty()) {
            return "";
        }

        if (errors.size() == 1) {
            return errors.getFirst();
        }

        return header + " with " + errors.size() + " error(s):\n" +
                errors.stream()
                        .map(e -> "  - " + e)
                        .collect(Collectors.joining("\n"));
    }

}
