package com.github.jawisimo.botengine.repository;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.jawisimo.botengine.config.BotProperties;
import com.github.jawisimo.botengine.interaction.node.model.DialogMap;
import com.github.jawisimo.botengine.loader.DialogLoader;
import com.github.jawisimo.botengine.interaction.node.model.DialogNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Slf4j
public class CaffeineDialogRepository implements DialogRepository {
  private final BotProperties properties;
  private final DialogLoader dialogLoader;

  private static final int CACHE_SIZE_MAX = 1;

  private final Cache<String, DialogMap> dialogCache =
      Caffeine.newBuilder().maximumSize(CACHE_SIZE_MAX).build();

  @Override
  public DialogNode getNode(String nodeId) {
    return getCachedDialogMap().getNode(nodeId);
  }

  private DialogMap getCachedDialogMap() {
    return dialogCache.get(properties.dialogFileName(), dialogLoader::load);
  }
}
