package com.jawisimo.tbcfstarter.loader;

import com.jawisimo.tbcfstarter.exception.DialogLoadingException;
import com.jawisimo.tbcfstarter.model.DialogMap;
import com.jawisimo.tbcfstarter.parser.DialogParser;
import com.jawisimo.tbcfstarter.validator.DialogValidator;
import com.jawisimo.tbcfstarter.validator.ResourceValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class DialogLoader {
    private final DialogValidator dialogValidator;
    private final ResourceValidator resourceValidator;
    private final List<DialogParser> parsers;

    public DialogMap loadDialog(String dialogFileName) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(dialogFileName)) {
            resourceValidator.validateDialogFile(is, dialogFileName);

            List<DialogParser> matchingParsers = parsers.stream()
                    .filter(s -> s.supports(dialogFileName))
                    .toList();

            resourceValidator.validateParserForFile(matchingParsers, dialogFileName);
            DialogParser strategy = matchingParsers.getFirst();
            log.info("Parsing '{}' using {}", dialogFileName, strategy.getClass().getSimpleName());
            DialogMap dialogMap = strategy.parse(is);
            dialogValidator.validateStartNode(dialogMap, dialogFileName);
            return dialogMap;

        } catch (Exception e) {
            throw new DialogLoadingException("Failed to load dialog file: " + dialogFileName, e);
        }
    }
}
