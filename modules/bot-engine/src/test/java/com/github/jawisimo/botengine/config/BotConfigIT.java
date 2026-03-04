package com.github.jawisimo.botengine.config;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@SpringBootTest(
    classes = {ApplicationConfig.class, BotConfig.class, BotConfigIT.TestCacheConfig.class})
@ActiveProfiles("test")
class BotConfigIT {

  @Autowired private ApplicationContext context;
  @Autowired private BotProperties properties;
  @Autowired private TelegramClient telegramClient;
  @Autowired private Executor virtualThreadsExecutor;

  @Test
  void context_shouldLoadPropertiesFromTestProfile_andCreateBeans() throws Exception {
    String token = "109846:shjf:test:token:telegram";
    String name = "TestBot";
    String dialogFileName = "dialog-test.yml";
    int buttonsPerRow = 1;
    boolean userStatePersistent = true;

    ExecutorService executorService =
        assertInstanceOf(ExecutorService.class, virtualThreadsExecutor);

    Future<Boolean> result = executorService.submit(() -> Thread.currentThread().isVirtual());

    assertNotNull(context);
    assertNotNull(properties);
    assertEquals(token, properties.token());
    assertEquals(name, properties.name());
    assertEquals(dialogFileName, properties.dialogFileName());
    assertEquals(buttonsPerRow, properties.buttonsPerRow());
    assertEquals(userStatePersistent, properties.userStatePersistent());
    assertNotNull(telegramClient);
    assertInstanceOf(OkHttpTelegramClient.class, telegramClient);
    assertNotNull(virtualThreadsExecutor);
    assertTrue(result.get());
    executorService.shutdownNow();
    executorService.close();
  }

  @Test
  void beans_shouldBeSingletons() {
    String asyncExecutorBeanName = "virtualThreadsExecutor";

    TelegramClient firstTelegramClient = context.getBean(TelegramClient.class);
    TelegramClient secondTelegramClient = context.getBean(TelegramClient.class);

    Executor firstExecutor = context.getBean(asyncExecutorBeanName, Executor.class);
    Executor secondExecutor = context.getBean(asyncExecutorBeanName, Executor.class);

    assertSame(firstTelegramClient, secondTelegramClient);
    assertSame(firstExecutor, secondExecutor);
  }

  @Test
  void context_shouldContainCacheManager_whenCachingEnabled() {
    CacheManager cacheManager = context.getBean(CacheManager.class);

    assertNotNull(cacheManager);
    assertInstanceOf(ConcurrentMapCacheManager.class, cacheManager);
  }

  @Configuration
  static class TestCacheConfig {
    @Bean
    CacheManager cacheManager() {
      return new ConcurrentMapCacheManager();
    }
  }
}
