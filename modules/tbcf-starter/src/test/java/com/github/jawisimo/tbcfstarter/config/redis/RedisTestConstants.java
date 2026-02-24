package com.github.jawisimo.tbcfstarter.config.redis;

public class RedisTestConstants {

  public static final String REDIS_DEPENDENCY_FAIL_MESSAGE =
      "❌ Redis is required but not found in the application context. "
          + "Please add spring-boot-starter-data-redis "
          + "and configure spring.data.redis.*properties.";

  public static final String REDIS_CONNECT_FAIL_MESSAGE =
      "❌ Redis dependency is present, but connection failed";

  public static final String REDIS_PING_FAIL_MESSAGE =
      "❌ Redis dependency is present, but cannot ping";

  public static final String SOME_ERROR_MESSAGE = "Some error message...";

  public static final String NOPE = "NOPE";
  public static final String PONG = "PONG";

  public static final String CONNECTION_FACTORY_NAME = "redisConnectionFactory";
}
