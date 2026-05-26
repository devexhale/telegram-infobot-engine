package io.github.devexhale.botengine.storage.warm;

import io.github.devexhale.botengine.diagnostics.exception.DefinitionInitializationException;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DefinitionStorageWarmer {

  private static final String BLOCK_SEPARATOR = System.lineSeparator().repeat(2);

  private final List<WarmableStorage> storages;

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
