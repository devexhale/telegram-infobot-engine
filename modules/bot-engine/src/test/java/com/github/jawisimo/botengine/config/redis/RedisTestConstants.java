package com.github.jawisimo.botengine.config.redis;

public class RedisTestConstants {

  public static final String REDIS_DEPENDENCY_FAIL_MSG =
      "Redis is required but not found in the application context. "
          + "Please add spring-boot-starter-data-redis "
          + "and configure spring.data.redis.*properties.";

  public static final String REDIS_CONNECT_FAIL_MSG =
      "Connection to Redis failed. Please check your connection.";

  public static final String SOME_ERROR_MSG = "Some error message...";

  public static final String NOPE = "NOPE";
  public static final String PONG = "PONG";

  public static final String CONNECT_FACTORY_NAME = "redisConnectionFactory";
}
