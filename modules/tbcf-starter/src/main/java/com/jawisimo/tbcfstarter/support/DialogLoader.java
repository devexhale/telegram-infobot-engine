package com.jawisimo.tbcfstarter.support;

import com.jawisimo.tbcfstarter.exception.DialogLoadingException;
import com.jawisimo.tbcfstarter.model.DialogNode;
import com.jawisimo.tbcfstarter.parser.DialogParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class DialogLoader {
    private static final String START_NOTE_ID = "/start";

    private final List<DialogParser> parsers;

    public Map<String, DialogNode> loadDialog(String dialogFileName) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(dialogFileName)) {
            if (is == null) {
                throw new DialogLoadingException("Dialog file not found: " + dialogFileName);
            }

            List<DialogParser> matchingParsers = parsers.stream()
                    .filter(s -> s.supports(dialogFileName))
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

            DialogParser strategy = matchingParsers.getFirst();

            log.info("Parsing '{}' using {}", dialogFileName, strategy.getClass().getSimpleName());

            Map<String, DialogNode> dialogMap = strategy.parse(is);

            if (!dialogMap.containsKey(START_NOTE_ID)) {
                throw new DialogLoadingException("Dialog must contain '/start' node in file: " + dialogFileName);
            }

            return dialogMap;

        } catch (Exception e) {
            throw new DialogLoadingException("Failed to load dialog file: " + dialogFileName, e);
        }
    }

}
