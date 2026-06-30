package io.github.devexhale.botengine.properties;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for the broadcast functionality.
 *
 * @param enabled whether the broadcast feature is enabled
 * @param fileName the path to the broadcast definition configuration file
 * @param timezone the timezone used for scheduling broadcasts
 * @since 1.0
 */
@ConfigurationProperties(prefix = "telegram.bot.broadcast")
@Slf4j
public record BroadcastProperties(boolean enabled, String fileName, String timezone) {}
