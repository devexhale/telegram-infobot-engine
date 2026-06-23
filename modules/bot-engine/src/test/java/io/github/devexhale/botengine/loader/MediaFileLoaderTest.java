package io.github.devexhale.botengine.loader;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.github.devexhale.botengine.diagnostics.exception.DefinitionInitializationException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.telegram.telegrambots.meta.api.objects.InputFile;

@ExtendWith(MockitoExtension.class)
class MediaFileLoaderTest {

  private static final String MEDIA_FOLDER = "content";

  @Mock private ResourceLoader resourceLoader;
  @Mock private Resource resource;

  @InjectMocks private MediaFileLoader mediaFileLoader;

  @Test
  void load_shouldReturnInputFile_whenResourceExistsAndReadable() throws IOException {
    String mediaFileName = "test.jpg";
    String expectedPath = Paths.get(MEDIA_FOLDER, mediaFileName).toString();
    InputStream mockInputStream = mock(InputStream.class);

    when(resourceLoader.getResource(expectedPath)).thenReturn(resource);
    when(resource.exists()).thenReturn(true);
    when(resource.getInputStream()).thenReturn(mockInputStream);

    InputFile result = mediaFileLoader.load(mediaFileName);

    assertNotNull(result);
  }

  @Test
  void load_shouldThrowException_whenResourceDoesNotExist() {
    String mediaFileName = "missing.jpg";
    String expectedPath = Paths.get(MEDIA_FOLDER, mediaFileName).toString();
    String expectedMsg = "Media file '%s/%s' not found".formatted(MEDIA_FOLDER, mediaFileName);

    when(resourceLoader.getResource(expectedPath)).thenReturn(resource);
    when(resource.exists()).thenReturn(false);

    DefinitionInitializationException exception =
        assertThrows(
            DefinitionInitializationException.class, () -> mediaFileLoader.load(mediaFileName));

    assertEquals(expectedMsg, exception.getMessage());
  }

  @Test
  void load_shouldThrowException_whenIOExceptionOccurs() throws IOException {
    String mediaFileName = "broken.jpg";
    String expectedPath = Paths.get(MEDIA_FOLDER, mediaFileName).toString();
    String expectedMsg = "Cannot read media file '%s/%s'".formatted(MEDIA_FOLDER, mediaFileName);

    when(resourceLoader.getResource(expectedPath)).thenReturn(resource);
    when(resource.exists()).thenReturn(true);
    when(resource.getInputStream()).thenThrow(new IOException("Read error"));

    DefinitionInitializationException exception =
        assertThrows(
            DefinitionInitializationException.class, () -> mediaFileLoader.load(mediaFileName));

    assertEquals(expectedMsg, exception.getMessage());
    assertNotNull(exception.getCause());
  }
}
