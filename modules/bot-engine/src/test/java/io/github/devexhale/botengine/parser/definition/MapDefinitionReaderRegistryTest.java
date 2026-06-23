package io.github.devexhale.botengine.parser.definition;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import io.github.devexhale.botengine.diagnostics.exception.DefinitionInitializationException;
import io.github.devexhale.botengine.domain.broadcast.BroadcastMap;
import io.github.devexhale.botengine.domain.dialog.DialogMap;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"rawtypes", "unchecked"})
class MapDefinitionReaderRegistryTest {

  @Mock private MapDefinitionReader dialogReader;

  @Mock private MapDefinitionReader broadcastReader;

  private MapDefinitionReaderRegistry registry;

  @BeforeEach
  void setUp() {
    when(dialogReader.targetType()).thenReturn(DialogMap.class);
    when(broadcastReader.targetType()).thenReturn(BroadcastMap.class);

    registry = new MapDefinitionReaderRegistry(List.of(dialogReader, broadcastReader));
  }

  @Test
  void get_shouldReturnDialogReader_whenDialogMapClassIsPassed() {
    MapDefinitionReader<DialogMap, ?> result = registry.get(DialogMap.class);

    assertEquals(dialogReader, result);
  }

  @Test
  void get_shouldReturnBroadcastReader_whenBroadcastMapClassIsPassed() {
    MapDefinitionReader<BroadcastMap, ?> result = registry.get(BroadcastMap.class);

    assertEquals(broadcastReader, result);
  }

  @Test
  void get_shouldThrowDefinitionInitializationException_whenTargetTypeIsNotRegistered() {
    DefinitionInitializationException exception =
        assertThrows(DefinitionInitializationException.class, () -> registry.get(String.class));

    assertTrue(exception.getMessage().contains("String"));
  }

  @Test
  void get_shouldThrowDefinitionInitializationException_whenRegistryIsEmpty() {
    MapDefinitionReaderRegistry emptyRegistry = new MapDefinitionReaderRegistry(List.of());

    assertThrows(DefinitionInitializationException.class, () -> emptyRegistry.get(DialogMap.class));
  }
}
