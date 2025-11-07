package com.jawisimo.tbcfstarter.config;

import com.jawisimo.tbcfstarter.exception.MissingPropertyException;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "telegram.bot")
public record BotProperties(String token,
                            String name,
                            String dialogFileName,
                            int buttonsPerRow,
                            boolean userStatePersistent) {

    private static final String BOT_TOKEN = "telegram.bot.token";
    private static final String BOT_NAME = "telegram.bot.name";
    private static final String BOT_BUTTONS_PER_ROW = "telegram.bot.buttons-per-row";

    public BotProperties {
        if (token == null || token.isBlank()) {
            throw new MissingPropertyException(BOT_TOKEN);
        }

        if (dialogFileName == null || dialogFileName.isBlank()) {
            throw new MissingPropertyException(BOT_NAME);
        }

        if (buttonsPerRow <= 0 || buttonsPerRow > 100) {
            throw new IllegalArgumentException(BOT_BUTTONS_PER_ROW + " must be a positive integer");
        }
    }
}
