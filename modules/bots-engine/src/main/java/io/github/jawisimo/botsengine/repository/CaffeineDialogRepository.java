package io.github.jawisimo.botsengine.repository;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.jawisimo.botsengine.config.bot.BotProperties;
import io.github.jawisimo.botsengine.model.DialogMap;
import io.github.jawisimo.botsengine.model.DialogNode;
import io.github.jawisimo.botsengine.loader.DialogLoader;
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
 * <p>The cache supports lazy loading via {@link #getNode(String)}. However, the dialog is eagerly
 * loaded during application startup by {@link #afterPropertiesSet()} to ensure parsing and
 * validation happen immediately (fail-fast).
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
   * Returns a dialog node from the cached dialog map.
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
    log.info("Warming up dialog cache for file: '{}'", properties.dialogFileName());
    getCachedDialogMap();
    log.info("Dialog cache warmed up successfully");
  }
}
