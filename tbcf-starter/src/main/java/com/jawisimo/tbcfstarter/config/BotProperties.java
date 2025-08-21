package com.jawisimo.tbcfstarter.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "telegram.bot")
public record BotProperties(String token,
                            String dialogFileName,
                            int buttonsPerRow,
                            int executorCorePoolSize,
                            int executorMaxPoolSize,
                            int executorQueueCapacity,
                            String executorThreadNamePrefix) {
}
