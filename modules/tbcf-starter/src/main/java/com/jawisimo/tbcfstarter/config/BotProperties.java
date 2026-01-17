package com.jawisimo.tbcfstarter.config;

import com.jawisimo.tbcfstarter.exception.MissingPropertyException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "telegram.bot")
@Slf4j
public record BotProperties(String token,
                            String name,
                            String dialogFileName,
                            int buttonsPerRow,
                            boolean userStatePersistent) {

    private static final String BOT_TOKEN = "telegram.bot.token";
    private static final String BOT_DIALOG_FILE_NAME = "telegram.bot.dialog-file-name";
    private static final String BUTTONS_PER_ROW = "telegram.bot.buttons-per-row";
    private static final int BUTTONS_PER_ROW_MIN_VALUE = 1;
    private static final int BUTTONS_PER_ROW_MAX_VALUE = 10;

    public BotProperties {
        if (token == null || token.isBlank()) {
            throw new MissingPropertyException(BOT_TOKEN);
        }

        if (dialogFileName == null || dialogFileName.isBlank()) {
            throw new MissingPropertyException(BOT_DIALOG_FILE_NAME);
        }

        if (buttonsPerRow < BUTTONS_PER_ROW_MIN_VALUE || buttonsPerRow > BUTTONS_PER_ROW_MAX_VALUE) {
            throw new IllegalArgumentException(
                    BUTTONS_PER_ROW + " must be between " + BUTTONS_PER_ROW_MIN_VALUE +
                            " and " + BUTTONS_PER_ROW_MAX_VALUE
            );
        }
    }
}
