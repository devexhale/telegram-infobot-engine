package io.github.devexhale.botengine.execution.common.content.handler;

import io.github.devexhale.botengine.domain.content.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class ContentHandlerRegistryTest {

  @Mock private ContentHandler audioHandler;
  @Mock private ContentHandler textHandler;
  @Mock private ContentHandler duplicateAudioHandler;

  @BeforeEach
  void setUp() {
    lenient().when(audioHandler.type()).thenReturn(ContentType.AUDIO);
    lenient().when(textHandler.type()).thenReturn(ContentType.TEXT);
    lenient().when(duplicateAudioHandler.type()).thenReturn(ContentType.AUDIO);
  }

  @Test
  void constructor_shouldRegisterHandlers_whenProvidedWithUniqueTypes() {
    ContentHandlerRegistry registry =
        new ContentHandlerRegistry(List.of(audioHandler, textHandler));

    assertEquals(Optional.of(audioHandler), registry.get(ContentType.AUDIO));
    assertEquals(Optional.of(textHandler), registry.get(ContentType.TEXT));
  }

  @Test
  void constructor_shouldThrowIllegalStateException_whenDuplicateTypesProvided() {
    List<ContentHandler> handlersWithDuplicate = List.of(audioHandler, duplicateAudioHandler);

    assertThrows(
        IllegalStateException.class, () -> new ContentHandlerRegistry(handlersWithDuplicate));
  }

  @Test
  void get_shouldReturnHandler_whenTypeIsRegistered() {
    ContentHandlerRegistry registry = new ContentHandlerRegistry(List.of(audioHandler));

    assertEquals(Optional.of(audioHandler), registry.get(ContentType.AUDIO));
  }

  @Test
  void get_shouldReturnEmpty_whenTypeIsNotRegistered() {
    ContentHandlerRegistry registry = new ContentHandlerRegistry(List.of(audioHandler));

    assertEquals(Optional.empty(), registry.get(ContentType.VIDEO));
  }
}
