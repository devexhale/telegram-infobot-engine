package com.jawisimo.tbcfstarter.loader;

import com.jawisimo.tbcfstarter.exception.DialogLoadingException;
import com.jawisimo.tbcfstarter.interaction.node.model.DialogMap;
import com.jawisimo.tbcfstarter.parser.DialogParser;
import com.jawisimo.tbcfstarter.parser.DialogParserProvider;
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
    private final DialogParserProvider dialogParserProvider;

    public DialogMap load(String dialogFileName) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(dialogFileName)) {
            if (is == null) {
                throw new DialogLoadingException("Dialog file not found: " + dialogFileName);
            }

            DialogParser parser = dialogParserProvider.getParser(dialogFileName);
            DialogMap dialogMap = parser.parse(is);
            dialogValidator.validateStartNode(dialogMap, dialogFileName);
            return dialogMap;
        } catch (Exception e) {
            throw new DialogLoadingException("Failed to load dialog file: " + dialogFileName, e);
        }
    }
}

