package io.github.jawisimo.botsengine.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryMessageRepositoryTest {

  private MessageRepository repository;

  @BeforeEach
  void init() {
    repository = new InMemoryMessageRepository();
  }

  @Test
  void shouldReturnSavedMessage_whenSingleMessageWasSaved() {
    String chatId = "250";
    Integer messageId = 12;

    repository.save(chatId, messageId);

    List<Integer> removed = repository.removeAll(chatId);

    assertNotNull(removed);
    assertEquals(List.of(messageId), removed);
  }

  @Test
  void shouldReturnMessagesInInsertionOrder_whenMultipleMessagesSavedForSameChat() {
    String chatId = "555";
    Integer firstMessageId = 10;
    Integer secondMessageId = 11;
    Integer thirdMessageId = 12;

    repository.save(chatId, firstMessageId);
    repository.save(chatId, secondMessageId);
    repository.save(chatId, thirdMessageId);

    List<Integer> removed = repository.removeAll(chatId);

    assertNotNull(removed);
    assertEquals(List.of(firstMessageId, secondMessageId, thirdMessageId), removed);
  }

  @Test
  void shouldReturnOnlyMessagesOfRequestedChat_whenMultipleChatsExist() {
    String firstChatId = "125";
    String secondChatId = "233";
    Integer firstChatMessageId = 14;
    Integer secondChatFirstMessageId = 26;
    Integer secondChatSecondMessageId = 21;

    repository.save(firstChatId, firstChatMessageId);
    repository.save(secondChatId, secondChatFirstMessageId);
    repository.save(secondChatId, secondChatSecondMessageId);

    List<Integer> removedFirstChat = repository.removeAll(firstChatId);
    List<Integer> removedSecondChat = repository.removeAll(secondChatId);

    assertNotNull(removedFirstChat);
    assertEquals(List.of(firstChatMessageId), removedFirstChat);
    assertNotNull(removedSecondChat);
    assertEquals(List.of(secondChatFirstMessageId, secondChatSecondMessageId), removedSecondChat);
  }

  @Test
  void shouldReturnEmptyList_whenChatDoesNotExist() {
    String chatId = "0";

    List<Integer> removed = repository.removeAll(chatId);

    assertNotNull(removed);
    assertTrue(removed.isEmpty());
  }

  @Test
  void shouldRemoveMessagesAfterFirstCall_whenCalledTwice() {
    String chatId = "46";
    Integer messageId = 98;

    repository.save(chatId, messageId);

    List<Integer> firstRemoved = repository.removeAll(chatId);
    List<Integer> secondRemoved = repository.removeAll(chatId);

    assertNotNull(firstRemoved);
    assertEquals(List.of(messageId), firstRemoved);
    assertNotNull(secondRemoved);
    assertTrue(secondRemoved.isEmpty());
  }
}
