package com.jawisimo.tbcfstarter.loader;

import com.jawisimo.tbcfstarter.exception.DialogLoadingException;
import com.jawisimo.tbcfstarter.dialog.node.model.Media;
import com.jawisimo.tbcfstarter.validator.DialogValidator;
import com.jawisimo.tbcfstarter.validator.ResourceValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class MediaFileLoader {
    private static final String MEDIA_FOLDER = "media";
    private final DialogValidator dialogValidator;
    private final ResourceValidator resourceValidator;

    public InputFile loadMedia(Media media) {
        String fileName = media.getFileName();
        dialogValidator.validateMediaFileName(fileName);
        String resourcePath = Paths.get(MEDIA_FOLDER, fileName).toString().replace('\\', '/');
        URL resourceUrl = getClass().getClassLoader().getResource(resourcePath);
        resourceValidator.validateMediaResource(resourceUrl, fileName);
        File file = convertUrlToFile(Objects.requireNonNull(resourceUrl), fileName);
        return new InputFile(file);
    }

    private File convertUrlToFile(URL resourceUrl, String fileName) {
        try {
            return new File(resourceUrl.toURI());
        } catch (URISyntaxException e) {
            throw new DialogLoadingException("Invalid URI for media file: " + fileName, e);
        }
    }
}
