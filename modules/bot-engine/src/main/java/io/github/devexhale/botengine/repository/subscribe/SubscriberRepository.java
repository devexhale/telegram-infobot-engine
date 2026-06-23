package io.github.devexhale.botengine.repository.subscribe;

import java.util.Set;

public interface SubscriberRepository {

  void save(String chatId);

  void delete(String chatId);

  Set<String> getAll();
}
