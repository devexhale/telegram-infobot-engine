package com.jawisimo.tbcfstarter.loader;

import com.jawisimo.tbcfstarter.exception.DialogLoadingException;
import com.jawisimo.tbcfstarter.interaction.node.model.DialogMap;
import com.jawisimo.tbcfstarter.parser.DialogParser;
import com.jawisimo.tbcfstarter.validator.DialogValidator;
import com.jawisimo.tbcfstarter.validator.ResourceValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DialogLoader {
    private final DialogValidator dialogValidator;
    private final ResourceValidator resourceValidator;
    private final List<DialogParser> parsers;

    public DialogMap load(String dialogFileName) {
        try (InputStream is = loadResource(dialogFileName)) {
            DialogParser parser = selectParser(dialogFileName);
            DialogMap dialogMap = parseDialog(is, parser);
            validateDialog(dialogMap, dialogFileName);
            return dialogMap;
        } catch (Exception e) {
            throw new DialogLoadingException("Failed to load dialog file: " + dialogFileName, e);
        }
    }

    private InputStream loadResource(String dialogFileName) {
        InputStream is = getClass().getClassLoader().getResourceAsStream(dialogFileName);
        resourceValidator.validateDialogFile(is, dialogFileName);
        return is;
    }

    private DialogParser selectParser(String dialogFileName) {
        List<DialogParser> matchingParsers = parsers.stream()
                .filter(p -> p.canParse(dialogFileName))
                .toList();
        resourceValidator.validateParserForFile(matchingParsers, dialogFileName);
        return matchingParsers.getFirst();
    }

    private DialogMap parseDialog(InputStream is, DialogParser parser) {
        log.info("Parsing dialog using {}", parser.getClass().getSimpleName());
        return parser.parse(is);
    }

    private void validateDialog(DialogMap dialogMap, String dialogFileName) {
        dialogValidator.validateStartNode(dialogMap, dialogFileName);
    }
}

