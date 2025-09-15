package com.jawisimo.tbcfstarter.support;

import com.jawisimo.tbcfstarter.exception.DialogLoadingException;
import com.jawisimo.tbcfstarter.model.DialogNode;
import com.jawisimo.tbcfstarter.parser.DialogParser;
import com.jawisimo.tbcfstarter.validator.DialogValidator;
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
    private final DialogValidator validator;
    private final List<DialogParser> parsers;

    public Map<String, DialogNode> loadDialog(String dialogFileName) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(dialogFileName)) {
            validator.validateDialogFile(is, dialogFileName);

            List<DialogParser> matchingParsers = parsers.stream()
                    .filter(s -> s.supports(dialogFileName))
                    .toList();

            validator.validateParsers(matchingParsers, dialogFileName);
            DialogParser strategy = matchingParsers.getFirst();
            log.info("Parsing '{}' using {}", dialogFileName, strategy.getClass().getSimpleName());
            Map<String, DialogNode> dialogMap = strategy.parse(is);
            validator.validateStartNode(dialogMap, dialogFileName);
            return dialogMap;

        } catch (Exception e) {
            throw new DialogLoadingException("Failed to load dialog file: " + dialogFileName, e);
        }
    }

}
