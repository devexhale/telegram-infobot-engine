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
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Repository;

/**
 * {@link DialogRepository} implementation backed by a Caffeine cache.
 *
 * <p>Loads dialog definitions using {@link DialogLoader} and caches the resulting {@link DialogMap}
 * to avoid repeated parsing of configuration files.
 *
 * <p>The dialog is normally loaded lazily on first access via {@link #getNode(String)}. The cache
 * is eagerly warmed up during application startup. See {@link #afterPropertiesSet()}. This ensures
 * dialog parsing and validation happen immediately on startup.
 *
 * <p>Since only a single dialog definition is expected, the cache is limited to one entry.
 *
 * @since 1.0
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class CaffeineDialogRepository implements DialogRepository, InitializingBean {

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

  /**
   * Warms up the dialog cache on application startup to ensure the dialog file is parsed and
   * validated eagerly (fail-fast) instead of on first runtime access.
   */
  @Override
  public void afterPropertiesSet() {
    log.info("Warming up dialog cache for file: {}", properties.dialogFileName());
    getCachedDialogMap();
    log.info("Dialog cache warmed up successfully");
  }
}
