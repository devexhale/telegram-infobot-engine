package com.jawisimo.tbcfstarter.repository;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryMessageRepository implements MessageRepository {
  private final ConcurrentHashMap<String, List<Integer>> storage = new ConcurrentHashMap<>();

  @Override
  public void save(String chatId, Integer messageId) {
    storage.compute(
        chatId,
        (k, v) -> {
          if (v == null) return new ArrayList<>(List.of(messageId));
          v.add(messageId);
          return v;
        });
  }

  @Override
  public List<Integer> removeAll(String chatId) {
    return storage.remove(chatId);
  }
}
