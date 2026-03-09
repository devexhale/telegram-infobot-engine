package com.github.jawisimo.botengine.repository;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.jawisimo.botengine.config.bot.BotProperties;
import com.github.jawisimo.botengine.model.DialogMap;
import com.github.jawisimo.botengine.model.DialogNode;
import com.github.jawisimo.botengine.loader.DialogLoader;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

/**
 * {@link DialogRepository} implementation backed by a Caffeine cache.
 *
 * <p>Loads dialog definitions using {@link DialogLoader} and caches the resulting {@link DialogMap}
 * to avoid repeated parsing of configuration files. The dialog is loaded lazily on first access and
 * reused for subsequent node lookups.
 *
 * @since 1.0
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class CaffeineDialogRepository implements DialogRepository {

  private final BotProperties properties;
  private final DialogLoader dialogLoader;

  private static final int CACHE_SIZE_MAX = 1;

  private final Cache<@NonNull String, DialogMap> dialogCache =
      Caffeine.newBuilder().maximumSize(CACHE_SIZE_MAX).build();

  /**
   * Returns a dialog node fromButton from the cached dialog map.
   *
   * @param nodeId the dialog node identifier
   * @return the corresponding {@link DialogNode}
   */
  @Override
  public DialogNode getNode(String nodeId) {
    return getCachedDialogMap().getNode(nodeId);
  }

  private DialogMap getCachedDialogMap() {
    return dialogCache.get(properties.dialogFileName(), dialogLoader::load);
  }
}
