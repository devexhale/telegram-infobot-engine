package io.github.devexhale.botengine.storage.warm;

public interface WarmableStorage {

  void warmUp();

  boolean isEnabled();
}
