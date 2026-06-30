package io.github.devexhale.botengine.storage.warm;

import io.github.devexhale.botengine.diagnostics.exception.DefinitionInitializationException;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Orchestrates the warm-up process for all registered {@link WarmableStorage} instances.
 *
 * <p>Iterates through available storages, triggers initialization for enabled ones, and aggregates
 * any errors encountered during the process.
 *
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DefinitionStorageWarmer {

  private static final String BLOCK_SEPARATOR = System.lineSeparator().repeat(2);

  private final List<WarmableStorage> storages;

  /**
   * Warms up all enabled storages and throws an exception if any initialization fails.
   *
   * @throws DefinitionInitializationException if one or more storages fail to warm up
   */
  public void warmUpAll() {
    List<String> errors = new ArrayList<>();

    for (WarmableStorage storage : storages) {
      if (storage.isEnabled()) {
        try {
          storage.warmUp();
        } catch (Exception e) {
          errors.add(e.getMessage());
        }
      }
    }

    if (!errors.isEmpty()) {
      String exceptionMsg = BLOCK_SEPARATOR + String.join(BLOCK_SEPARATOR, errors);
      throw new DefinitionInitializationException(exceptionMsg);
    }
  }
}
