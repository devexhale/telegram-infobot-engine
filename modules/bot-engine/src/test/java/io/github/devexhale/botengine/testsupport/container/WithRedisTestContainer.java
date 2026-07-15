package io.github.devexhale.botengine.testsupport.container;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.boot.data.redis.autoconfigure.DataRedisRepositoriesAutoConfiguration;
import org.springframework.boot.data.redis.test.autoconfigure.DataRedisTest;
import org.springframework.context.annotation.Import;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(RedisTestContainerConfiguration.class)
@DataRedisTest(excludeAutoConfiguration = DataRedisRepositoriesAutoConfiguration.class)
public @interface WithRedisTestContainer {}
