package io.github.devexhale.botengine.properties;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "telegram.bot.dialog")
@Slf4j
public record DialogProperties(String fileName, int buttonsPerRow) {}
