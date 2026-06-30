package io.github.devexhale.botengine.storage.warm;

/**
 * Defines the contract for in-memory storages that require pre-loading of data during application
 * startup.
 *
 * @since 1.0
 */
public interface WarmableStorage {

  /** Loads and caches the underlying data into memory. */
  void warmUp();

  /**
   * Checks whether this storage is currently enabled.
   *
   * @return {@code true} if the storage is enabled and should be warmed up, {@code false} otherwise
   */
  boolean isEnabled();
}
