package io.github.devexhale.botengine.storage.warm;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import io.github.devexhale.botengine.diagnostics.exception.DefinitionInitializationException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DefinitionStorageWarmerTest {

  private static final String ERROR_MSG_1 = "Failed to warm storage A";
  private static final String ERROR_MSG_2 = "Failed to warm storage B";

  @Mock private WarmableStorage storageA;
  @Mock private WarmableStorage storageB;
  @Mock private WarmableStorage storageC;

  @InjectMocks private DefinitionStorageWarmer warmer;

  @Test
  void warmUpAll_shouldWarmEnabledStorages_whenAllSucceed() {
    when(storageA.isEnabled()).thenReturn(true);
    when(storageB.isEnabled()).thenReturn(true);

    warmer = new DefinitionStorageWarmer(List.of(storageA, storageB));

    assertDoesNotThrow(warmer::warmUpAll);
  }

  @Test
  void warmUpAll_shouldSkipDisabledStorages_whenCalled() {
    when(storageA.isEnabled()).thenReturn(true);
    when(storageB.isEnabled()).thenReturn(false);
    when(storageC.isEnabled()).thenReturn(true);

    warmer = new DefinitionStorageWarmer(List.of(storageA, storageB, storageC));

    assertDoesNotThrow(warmer::warmUpAll);
  }

  @Test
  void warmUpAll_shouldCollectAndThrowException_whenSingleStorageFails() {
    when(storageA.isEnabled()).thenReturn(true);
    when(storageB.isEnabled()).thenReturn(true);
    doThrow(new RuntimeException(ERROR_MSG_1)).when(storageA).warmUp();

    warmer = new DefinitionStorageWarmer(List.of(storageA, storageB));

    DefinitionInitializationException exception =
        assertThrows(DefinitionInitializationException.class, warmer::warmUpAll);

    assertTrue(exception.getMessage().contains(ERROR_MSG_1));
  }

  @Test
  void warmUpAll_shouldCollectAndThrowAllExceptions_whenMultipleStoragesFail() {
    when(storageA.isEnabled()).thenReturn(true);
    when(storageB.isEnabled()).thenReturn(true);
    when(storageC.isEnabled()).thenReturn(true);
    doThrow(new RuntimeException(ERROR_MSG_1)).when(storageA).warmUp();
    doThrow(new RuntimeException(ERROR_MSG_2)).when(storageC).warmUp();

    warmer = new DefinitionStorageWarmer(List.of(storageA, storageB, storageC));

    DefinitionInitializationException exception =
        assertThrows(DefinitionInitializationException.class, warmer::warmUpAll);

    String message = exception.getMessage();
    assertTrue(message.contains(ERROR_MSG_1));
    assertTrue(message.contains(ERROR_MSG_2));
  }

  @Test
  void warmUpAll_shouldContinueAfterFailure_whenOneStorageFails() {
    when(storageA.isEnabled()).thenReturn(true);
    when(storageB.isEnabled()).thenReturn(true);
    doThrow(new RuntimeException(ERROR_MSG_1)).when(storageA).warmUp();

    warmer = new DefinitionStorageWarmer(List.of(storageA, storageB));

    assertThrows(DefinitionInitializationException.class, warmer::warmUpAll);
  }

  @Test
  void warmUpAll_shouldDoNothing_whenNoStoragesProvided() {
    warmer = new DefinitionStorageWarmer(List.of());

    assertDoesNotThrow(warmer::warmUpAll);
  }

  @Test
  void warmUpAll_shouldDoNothing_whenAllStoragesAreDisabled() {
    lenient().when(storageA.isEnabled()).thenReturn(false);
    lenient().when(storageB.isEnabled()).thenReturn(false);

    warmer = new DefinitionStorageWarmer(List.of(storageA, storageB));

    assertDoesNotThrow(warmer::warmUpAll);
  }
}
