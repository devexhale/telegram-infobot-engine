package io.github.jawisimo.botsengine.repository;

import java.util.Set;

public interface SubscriberRepository {

  void add(String chatId);

  void remove(String chatId);

  Set<String> getAll();
}
