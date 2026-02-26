package com.github.jawisimo.botengine.repository;

import java.util.List;

public interface MessageRepository {

  void save(String chatId, Integer messageId);

  List<Integer> removeAll(String chatId);
}
