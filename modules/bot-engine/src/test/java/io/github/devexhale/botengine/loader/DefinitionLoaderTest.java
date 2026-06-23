package io.github.devexhale.botengine.loader;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import io.github.devexhale.botengine.diagnostics.exception.DefinitionInitializationException;
import io.github.devexhale.botengine.domain.dialog.DialogMap;
import io.github.devexhale.botengine.parser.MapParser;
import io.github.devexhale.botengine.validator.definition.DefinitionValidatorRegistry;
import io.github.devexhale.botengine.validator.definition.DialogValidator;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

@ExtendWith(MockitoExtension.class)
class DefinitionLoaderTest {

  private static final String FILE_NAME = "dialog.yaml";
  private static final Class<DialogMap> TARGET_TYPE = DialogMap.class;

  @Mock private MapParser mapParser;
  @Mock private DefinitionValidatorRegistry validatorRegistry;
  @Mock private ResourceLoader resourceLoader;
  @Mock private Resource resource;

  @InjectMocks private DefinitionLoader loader;

  @Mock private DialogValidator validator;
  @Mock private DialogMap config;

  @Test
  void load_shouldThrowException_whenResourceDoesNotExist() {
    when(resourceLoader.getResource(FILE_NAME)).thenReturn(resource);
    when(resource.exists()).thenReturn(false);

    DefinitionInitializationException exception =
        assertThrows(
            DefinitionInitializationException.class, () -> loader.load(FILE_NAME, TARGET_TYPE));

    assertTrue(exception.getMessage().contains(FILE_NAME));
  }

  @Test
  void load_shouldThrowException_whenResourceContentIsEmpty() throws IOException {
    when(resourceLoader.getResource(FILE_NAME)).thenReturn(resource);
    when(resource.exists()).thenReturn(true);
    when(resource.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[0]));

    DefinitionInitializationException exception =
        assertThrows(
            DefinitionInitializationException.class, () -> loader.load(FILE_NAME, TARGET_TYPE));

    assertTrue(exception.getMessage().contains(FILE_NAME));
  }

  @Test
  void load_shouldParseAndReturnConfig_whenResourceIsValidAndValidatorIsNull() throws IOException {
    byte[] content = "content".getBytes();

    when(resourceLoader.getResource(FILE_NAME)).thenReturn(resource);
    when(resource.exists()).thenReturn(true);
    when(resource.getInputStream()).thenReturn(new ByteArrayInputStream(content));
    when(mapParser.parse(eq(FILE_NAME), any(InputStream.class), eq(TARGET_TYPE)))
        .thenReturn(config);
    when(validatorRegistry.get(TARGET_TYPE)).thenReturn(null);

    DialogMap result = loader.load(FILE_NAME, TARGET_TYPE);

    assertEquals(config, result);
    verify(validatorRegistry).get(TARGET_TYPE);
    verifyNoMoreInteractions(validatorRegistry);
  }

  @Test
  void load_shouldParseValidateAndReturnConfig_whenResourceIsValidAndValidatorIsNotNull()
      throws IOException {
    byte[] content = "content".getBytes();

    when(resourceLoader.getResource(FILE_NAME)).thenReturn(resource);
    when(resource.exists()).thenReturn(true);
    when(resource.getInputStream()).thenReturn(new ByteArrayInputStream(content));
    when(mapParser.parse(eq(FILE_NAME), any(InputStream.class), eq(TARGET_TYPE)))
        .thenReturn(config);
    when(validatorRegistry.get(TARGET_TYPE)).thenReturn(validator);

    DialogMap result = loader.load(FILE_NAME, TARGET_TYPE);

    assertEquals(config, result);
    verify(validator).validate(config, FILE_NAME);
  }

  @Test
  void load_shouldThrowException_whenIOExceptionOccurs() throws IOException {
    when(resourceLoader.getResource(FILE_NAME)).thenReturn(resource);
    when(resource.exists()).thenReturn(true);
    when(resource.getInputStream()).thenThrow(new IOException("Read error"));

    DefinitionInitializationException exception =
        assertThrows(
            DefinitionInitializationException.class, () -> loader.load(FILE_NAME, TARGET_TYPE));

    assertTrue(exception.getMessage().contains(FILE_NAME));
    assertTrue(exception.getMessage().contains(TARGET_TYPE.getSimpleName()));
  }
}
