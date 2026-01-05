//package com.jawisimo.tbcfstarter.config.redis;
//
//import com.jawisimo.tbcfstarter.config.BotProperties;
//import com.jawisimo.tbcfstarter.exception.RedisConnectionException;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.connection.RedisConnection;
//import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
//
//@Configuration
//@RequiredArgsConstructor
//@Slf4j
//public class RedisConnectionCheckConfig {
//
//    private static final String REDIS_CANNOT_CONNECT = "❌ Redis is enabled but cannot connect";
//    private static final String REDIS_CANNOT_PING = "❌ Redis is enabled but cannot ping";
//    private static final String REDIS_PONG = "PONG";
//
//    private final BotProperties properties;
//    private final LettuceConnectionFactory redisConnectionFactory;
//
//    @Bean
//    public ApplicationRunner redisPingRunner() {
//        return args -> {
//            if (!properties.userStatePersistent()) {
//                return;
//            }
//
//            try (RedisConnection redisConnection = redisConnectionFactory.getConnection()) {
//                String pongResponse = redisConnection.ping();
//
//                if (!REDIS_PONG.equalsIgnoreCase(pongResponse)) {
//                    log.error(REDIS_CANNOT_PING);
//                    throw new RedisConnectionException(REDIS_CANNOT_PING);
//                }
//            } catch (Exception e) {
//                log.error(REDIS_CANNOT_CONNECT, e);
//                throw new RedisConnectionException(REDIS_CANNOT_CONNECT, e);
//            }
//        };
//    }
//}
