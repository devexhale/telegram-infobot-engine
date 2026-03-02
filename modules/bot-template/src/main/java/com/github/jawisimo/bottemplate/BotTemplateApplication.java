package com.github.jawisimo.bottemplate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point of the template Telegram bot application.
 *
 * <p>Bootstraps a bot powered by the telegram-dialog-bot-spring-boot-starter. The bot behavior is
 * defined via dialog configuration files located in resources, while optional customization is
 * provided through application properties.
 *
 * <p>No additional Java code is required for basic dialog bots.
 *
 * @since 1.0
 */
@SpringBootApplication
public class BotTemplateApplication {

  public static void main(String[] args) {
    SpringApplication.run(BotTemplateApplication.class, args);
  }
}
