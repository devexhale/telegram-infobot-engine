package com.github.jawisimo.tbcfstarter.repository;

import com.github.jawisimo.tbcfstarter.config.redis.RedisRepositoriesConfig;
import com.github.jawisimo.tbcfstarter.container.WithRedisTestContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

@DataRedisTest
@WithRedisTestContainer
@ContextConfiguration(classes = {RedisRepositoriesConfig.class, RedisUserStateRepository.class})
@TestPropertySource(properties = {"telegram.bot.user-state-persistent=true"})
class RedisUserStateRepositoryIT extends BaseUserStateRepositoryTest {

  @Autowired private RedisUserStateRepository repository;
  @Autowired private RedisConnectionFactory connectionFactory;

  @Override
  protected UserStateRepository getRepository() {
    return repository;
  }

  @Override
  protected void cleanUp() {
    connectionFactory.getConnection().serverCommands().flushDb();
  }
}
