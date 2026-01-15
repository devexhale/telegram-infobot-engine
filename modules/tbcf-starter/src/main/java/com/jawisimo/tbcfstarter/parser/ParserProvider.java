package com.jawisimo.tbcfstarter.parser;

import com.jawisimo.tbcfstarter.exception.DialogLoadingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ParserProvider {
    private final List<DialogParser> parsers;

    public DialogParser get(String dialogFileName) {
        List<DialogParser> matchingParsers = parsers.stream()
                .filter(p -> p.canParse(dialogFileName))
                .toList();

        if (matchingParsers.isEmpty()) {
            throw new DialogLoadingException("No suitable parser found for file: " + dialogFileName);
        }

        if (matchingParsers.size() > 1) {
            throw new DialogLoadingException(
                    "Multiple parsers found for file: " + dialogFileName +
                            " -> " + matchingParsers.stream()
                            .map(p -> p.getClass().getSimpleName())
                            .toList()
            );
        }


        return matchingParsers.getFirst();
    }
}
