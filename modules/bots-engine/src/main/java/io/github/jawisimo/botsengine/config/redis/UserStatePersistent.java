package io.github.jawisimo.botsengine.config.redis;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

/**
 * Conditional annotation that enables components when persistent user state is active.
 *
 * <p>Shortcut for {@link ConditionalOnProperty} with {@code
 * telegram.bot.user-state-persistent=true}.
 *
 * @since 1.0
 */
@ConditionalOnProperty(
    prefix = "telegram.bot",
    name = "user-state-persistent",
    havingValue = "true")
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UserStatePersistent {}
