package com.jawisimo.tbcfstarter.support;

import com.jawisimo.tbcfstarter.exception.DialogLoadingException;
import com.jawisimo.tbcfstarter.model.Media;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

@Component
public class MediaFileLoader {
    private static final String MEDIA_FOLDER = "media";

    public InputFile loadMedia(Media media) {
        String fileName = media.getFileName();
        String resourcePath = Paths.get(MEDIA_FOLDER, fileName).toString().replace('\\', '/');

        URL resourceUrl = getClass().getClassLoader().getResource(resourcePath);
        if (resourceUrl == null) {
            throw new DialogLoadingException("Media file not found: " + fileName);
        }

        File file;
        try {
            file = new File(resourceUrl.toURI());
        } catch (URISyntaxException e) {
            throw new DialogLoadingException("Invalid URI for media file: " + fileName, e);
        }

        return new InputFile(file);
    }

}
