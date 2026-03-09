package com.github.jawisimo.botengine.config.appication;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

/**
 * Autoconfiguration entry point for the bot-engine Spring Boot starter.
 *
 * <p>Registers framework components via component scanning and loads default starter properties
 * from {@code application-starter.properties}.
 *
 * @since 1.0
 */
@AutoConfiguration
@ComponentScan(basePackages = "com.github.jawisimo.botengine")
@PropertySource("classpath:application-starter.properties")
public class StarterAutoConfig {}
