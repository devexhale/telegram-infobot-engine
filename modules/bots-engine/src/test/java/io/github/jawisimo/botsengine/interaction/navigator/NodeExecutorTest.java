package io.github.jawisimo.botsengine.interaction.navigator;

import io.github.jawisimo.botsengine.interaction.keyboard.KeyboardExecutor;
import io.github.jawisimo.botsengine.interaction.content.ContentExecutor;
import io.github.jawisimo.botsengine.model.Button;
import io.github.jawisimo.botsengine.model.ButtonType;
import io.github.jawisimo.botsengine.model.DialogNode;
import io.github.jawisimo.botsengine.service.MessageCleanupService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NodeExecutorTest {

  private static final String CHAT_ID = "123L";
  private static final String CHAT_ID_2 = "456L";

  private static final String MESSAGE = "hello";
  private static final String BTN_LABEL = "btn";
  private static final String BTN_NEXT = "next";
  private static final String BTN_URL = "https://example.com";

  private static final int THREADS_COUNT = 2;
  private static final int EXPECTED_SINGLE_THREAD_INSIDE_LOCK = 1;
  private static final int EXECUTIONS_COUNT = 2;
  private static final long TIMEOUT_SECONDS = 2L;
  private static final int INITIAL_CONCURRENT_VALUE = 0;

  @Mock private ContentExecutor contentExecutor;
  @Mock private KeyboardExecutor keyboardExecutor;
  @Mock private MessageCleanupService cleanupService;

  @InjectMocks private NodeExecutor nodeExecutor;

  private DialogNode node;

  @BeforeEach
  void init() {
    Button button = new Button(BTN_LABEL, BTN_NEXT, BTN_URL);
    node = new DialogNode(null, MESSAGE, ButtonType.INLINE, List.of(button));
  }

  @Test
  void execute_shouldCallCleanupThenMediaThenKeyboard_whenInvoked() {
    nodeExecutor.execute(node, CHAT_ID);

    InOrder inOrder = inOrder(cleanupService, contentExecutor, keyboardExecutor);
    inOrder.verify(cleanupService).clearLastNode(CHAT_ID);
    inOrder.verify(contentExecutor).execute(node, CHAT_ID);
    inOrder.verify(keyboardExecutor).execute(node, CHAT_ID);
  }

  @Test
  void execute_shouldSerializeCallsForSameChatId_whenCalledConcurrently() throws Exception {
    CountDownLatch firstEntered = new CountDownLatch(EXPECTED_SINGLE_THREAD_INSIDE_LOCK);
    CountDownLatch releaseFirst = new CountDownLatch(EXPECTED_SINGLE_THREAD_INSIDE_LOCK);

    AtomicInteger concurrentInside = new AtomicInteger(INITIAL_CONCURRENT_VALUE);
    AtomicInteger maxConcurrentInside = new AtomicInteger(INITIAL_CONCURRENT_VALUE);

    doAnswer(
            invocation -> {
              firstEntered.countDown();
              int current = concurrentInside.incrementAndGet();
              maxConcurrentInside.updateAndGet(prev -> Math.max(prev, current));

              boolean released = releaseFirst.await(TIMEOUT_SECONDS, TimeUnit.SECONDS);

              concurrentInside.decrementAndGet();
              assertTrue(released);
              return null;
            })
        .when(cleanupService)
        .clearLastNode(CHAT_ID);

    ExecutorService pool = Executors.newFixedThreadPool(THREADS_COUNT);

    Future<?> f1 = pool.submit(() -> nodeExecutor.execute(node, CHAT_ID));
    boolean entered = firstEntered.await(TIMEOUT_SECONDS, TimeUnit.SECONDS);

    Future<?> f2 = pool.submit(() -> nodeExecutor.execute(node, CHAT_ID));

    releaseFirst.countDown();

    f1.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
    f2.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);

    pool.shutdownNow();
    pool.close();

    assertTrue(entered);
    assertEquals(EXPECTED_SINGLE_THREAD_INSIDE_LOCK, maxConcurrentInside.get());
    verify(cleanupService, times(EXECUTIONS_COUNT)).clearLastNode(CHAT_ID);
    verify(contentExecutor, times(EXECUTIONS_COUNT)).execute(node, CHAT_ID);
    verify(keyboardExecutor, times(EXECUTIONS_COUNT)).execute(node, CHAT_ID);
  }

  @Test
  void execute_shouldNotBlockDifferentChatIds_whenCalledConcurrently() throws Exception {
    CountDownLatch enteredChat1 = new CountDownLatch(EXPECTED_SINGLE_THREAD_INSIDE_LOCK);
    CountDownLatch enteredChat2 = new CountDownLatch(EXPECTED_SINGLE_THREAD_INSIDE_LOCK);
    CountDownLatch release = new CountDownLatch(EXPECTED_SINGLE_THREAD_INSIDE_LOCK);

    doAnswer(
            invocation -> {
              String chatId = invocation.getArgument(INITIAL_CONCURRENT_VALUE, String.class);

              if (CHAT_ID.equals(chatId)) {
                enteredChat1.countDown();
                boolean released = release.await(TIMEOUT_SECONDS, TimeUnit.SECONDS);
                assertTrue(released);
              }

              if (CHAT_ID_2.equals(chatId)) {
                enteredChat2.countDown();
                boolean released = release.await(TIMEOUT_SECONDS, TimeUnit.SECONDS);
                assertTrue(released);
              }

              return null;
            })
        .when(cleanupService)
        .clearLastNode(anyString());

    ExecutorService pool = Executors.newFixedThreadPool(THREADS_COUNT);

    Future<?> f1 = pool.submit(() -> nodeExecutor.execute(node, CHAT_ID));
    Future<?> f2 = pool.submit(() -> nodeExecutor.execute(node, CHAT_ID_2));

    boolean entered1 = enteredChat1.await(TIMEOUT_SECONDS, TimeUnit.SECONDS);
    boolean entered2 = enteredChat2.await(TIMEOUT_SECONDS, TimeUnit.SECONDS);

    release.countDown();

    f1.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
    f2.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);

    pool.shutdownNow();
    pool.close();

    assertTrue(entered1);
    assertTrue(entered2);
    verify(cleanupService).clearLastNode(CHAT_ID);
    verify(cleanupService).clearLastNode(CHAT_ID_2);
    verify(contentExecutor).execute(node, CHAT_ID);
    verify(contentExecutor).execute(node, CHAT_ID_2);
    verify(keyboardExecutor).execute(node, CHAT_ID);
    verify(keyboardExecutor).execute(node, CHAT_ID_2);
  }
}
