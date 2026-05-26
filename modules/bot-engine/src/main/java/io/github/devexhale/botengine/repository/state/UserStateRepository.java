package io.github.devexhale.botengine.repository.state;

import java.util.Optional;

public interface UserStateRepository {

  Optional<String> findNodeKey(String chatId);

  void save(String chatId, String nodeId);

  void delete(String chatId);
}
