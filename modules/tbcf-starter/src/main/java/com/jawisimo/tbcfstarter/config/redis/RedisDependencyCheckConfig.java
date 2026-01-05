//package com.jawisimo.tbcfstarter.config.redis;
//
//import com.jawisimo.tbcfstarter.annotation.UserStatePersistent;
//import com.jawisimo.tbcfstarter.exception.RedisConnectionException;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Slf4j
//@Configuration
//@UserStatePersistent
//public class RedisDependencyCheckConfig {
//
//    private static final String REDIS_FAIL_MESSAGE =
//            "❌ Redis is required but not found in the application context. " +
//            "Please add spring-boot-starter-data-redis " +
//            "and configure spring.data.redis.*properties.";
//
//
//    @Bean
//    public ApplicationRunner redisFailFastRunner(ApplicationContext context) {
//        return args -> {
//            if (!context.containsBean("redisConnectionFactory")) {
//                log.error(REDIS_FAIL_MESSAGE);
//                throw new RedisConnectionException(REDIS_FAIL_MESSAGE);
//            }
//        };
//    }
//}
