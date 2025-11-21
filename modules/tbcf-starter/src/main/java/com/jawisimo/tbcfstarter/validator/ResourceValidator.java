package com.jawisimo.tbcfstarter.validator;

import com.jawisimo.tbcfstarter.exception.DialogLoadingException;
import com.jawisimo.tbcfstarter.parser.DialogParser;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;

@Component
public class ResourceValidator {

    public void validateDialogFile(InputStream is, String fileName) {
        if (is == null) {
            throw new DialogLoadingException("Dialog file not found: " + fileName);
        }
    }

    public void validateMediaFile(InputStream is, String fileName) {
        if (is == null) {
            throw new DialogLoadingException("Media file not found: " + fileName);
        }
    }

    public void validateParserForFile(List<DialogParser> matchingParsers, String fileName) {
        if (matchingParsers.isEmpty()) {
            throw new DialogLoadingException("No suitable parser found for file: " + fileName);
        }

        if (matchingParsers.size() > 1) {
            throw new DialogLoadingException(
                    "Multiple parsers found for file: " + fileName +
                            " -> " + matchingParsers.stream()
                            .map(p -> p.getClass().getSimpleName())
                            .toList()
            );
        }
    }
}
