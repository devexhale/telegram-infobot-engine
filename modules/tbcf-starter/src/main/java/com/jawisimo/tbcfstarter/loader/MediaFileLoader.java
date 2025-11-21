package com.jawisimo.tbcfstarter.loader;

import com.jawisimo.tbcfstarter.validator.DialogValidator;
import com.jawisimo.tbcfstarter.validator.ResourceValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.InputStream;
import java.nio.file.Paths;

@Component
@RequiredArgsConstructor
@Slf4j
public class MediaFileLoader {
    private static final String MEDIA_FOLDER = "media";
    private final DialogValidator dialogValidator;
    private final ResourceValidator resourceValidator;

    public InputFile load(String mediaFileName) {
        dialogValidator.validateMediaFileName(mediaFileName);
        String resourcePath = Paths.get(MEDIA_FOLDER, mediaFileName).toString();
        InputStream is = getClass().getClassLoader().getResourceAsStream(resourcePath);
        resourceValidator.validateMediaFile(is, mediaFileName);
        return new InputFile(is, mediaFileName);
    }
}
