package com.jawisimo.tbcfstarter.loader;

import com.jawisimo.tbcfstarter.exception.DialogLoadingException;
import com.jawisimo.tbcfstarter.interaction.node.model.DialogMap;
import com.jawisimo.tbcfstarter.parser.DialogParser;
import com.jawisimo.tbcfstarter.parser.ParserProvider;
import com.jawisimo.tbcfstarter.validator.DialogValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
@RequiredArgsConstructor
@Slf4j
public class DialogLoader {
    private final DialogValidator dialogValidator;
    private final ParserProvider parserProvider;

    public DialogMap load(String dialogFileName) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(dialogFileName)) {
            DialogParser parser = parserProvider.get(dialogFileName);
            log.info("Parsing dialog using {}", parser.getClass().getSimpleName());
            DialogMap dialogMap = parser.parse(is);
            dialogValidator.validateStartNode(dialogMap, dialogFileName);
            return dialogMap;
        } catch (Exception e) {
            throw new DialogLoadingException("Failed to load dialog file: " + dialogFileName, e);
        }
    }
}

