package io.github.devexhale.botengine.properties;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "telegram.bot.broadcast")
@Slf4j
public record BroadcastProperties(boolean enabled, String fileName, String timezone) {}
