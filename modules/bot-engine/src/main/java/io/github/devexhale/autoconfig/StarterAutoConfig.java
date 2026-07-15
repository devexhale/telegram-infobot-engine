package io.github.devexhale.autoconfig;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator;
import org.springframework.context.annotation.PropertySource;

/**
 * Autoconfiguration entry point for the bot-engine Spring Boot starter.
 *
 * <p>Registers framework components via component scanning and loads default starter properties
 * from {@code telegram-infobot-engine-defaults.properties}.
 *
 * <p>Uses fully qualified bean names to prevent naming collisions with consumer applications.
 *
 * @since 1.0
 */
@AutoConfiguration
@ComponentScan(
    basePackages = "io.github.devexhale.botengine",
    nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class)
@PropertySource("classpath:telegram-infobot-engine-defaults.properties")
public class StarterAutoConfig {}
