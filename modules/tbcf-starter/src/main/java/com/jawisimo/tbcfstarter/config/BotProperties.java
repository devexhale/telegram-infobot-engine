package com.jawisimo.tbcfstarter.config;

import com.jawisimo.tbcfstarter.exception.MissingPropertyException;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "telegram.bot")
public record BotProperties(String token,
                            String name,
                            String dialogFileName,
                            int buttonsPerRow,
                            int executorCorePoolSize,
                            int executorMaxPoolSize,
                            int executorQueueCapacity,
                            String executorThreadNamePrefix,
                            String enableLastCommand) {

    private static final String TELEGRAM_BOT_TOKEN_PROPERTY = "telegram.bot.token";
    private static final String TELEGRAM_BOT_NAME_PROPERTY = "telegram.bot.name";

    public BotProperties {
        if (token == null || token.isBlank()) {
            throw new MissingPropertyException(TELEGRAM_BOT_TOKEN_PROPERTY);
        }
        if (dialogFileName == null || dialogFileName.isBlank()) {
            throw new MissingPropertyException(TELEGRAM_BOT_NAME_PROPERTY);
        }
    }

}
