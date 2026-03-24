package io.github.jawisimo.botsengine.service;

import io.github.jawisimo.botsengine.repository.SubscriberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class SubscriberService {

  private final SubscriberRepository repository;

  public void subscribe(String chatId) {
    repository.add(chatId);
  }

  public void unsubscribe(String chatId) {
    repository.remove(chatId);
  }

  public Set<String> getAll() {
    return repository.getAll();
  }
}
