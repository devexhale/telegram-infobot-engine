package io.github.devexhale.botengine.properties;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "telegram.bot")
@Slf4j
public record BotProperties(String token, String name, RateLimit rateLimit) {

  public record RateLimit(Global global, Chat chat) {

    public record Global(int capacity, int rate, Duration interval) {}

    public record Chat(int capacity, int rate, Duration interval) {}
  }
}
