package com.github.jawisimo.botengine.config.application;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import com.github.jawisimo.botengine.config.appication.ApplicationConfig;
import org.junit.jupiter.api.Test;

class ApplicationConfigTest {

  @Test
  void virtualThreadsExecutor_shouldExecuteTaskOnVirtualThread() throws Exception {
    int index = 0;
    int count = 1;
    int awaitSeconds = 2;

    ApplicationConfig config = new ApplicationConfig();
    Executor executor = config.virtualThreadsExecutor();

    CountDownLatch latch = new CountDownLatch(count);
    Thread[] threadHolder = new Thread[count];

    executor.execute(
        () -> {
          threadHolder[index] = Thread.currentThread();
          latch.countDown();
        });

    assertTrue(latch.await(awaitSeconds, TimeUnit.SECONDS));
    assertNotNull(threadHolder[index]);
    assertTrue(threadHolder[index].isVirtual());
  }
}
