package io.github.devexhale.botengine.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

/**
 * Enables components when broadcast functionality is active.
 *
 * <p>Shortcut for {@link ConditionalOnProperty} with {@code telegram.bot.broadcast.enabled}.
 *
 * @since 1.0
 */
@ConditionalOnProperty(prefix = "telegram.bot.broadcast", name = "enabled", havingValue = "true")
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ConditionalOnBroadcastEnabled {}
