package io.github.devexhale.botengine.properties;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for the dialog functionality.
 *
 * @param fileName the path to the dialog definition configuration file
 * @param buttonsPerRow the maximum number of buttons per row in keyboards
 * @since 1.0
 */
@ConfigurationProperties(prefix = "telegram.bot.dialog")
@Slf4j
public record DialogProperties(String fileName, int buttonsPerRow) {}
