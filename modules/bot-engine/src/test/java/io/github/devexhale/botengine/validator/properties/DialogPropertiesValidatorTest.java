package io.github.devexhale.botengine.validator.properties;

import io.github.devexhale.botengine.properties.DialogProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DialogPropertiesValidatorTest {

  private static final String VALID_FILE_NAME = "dialog.yaml";
  private static final String INVALID_FILE_NAME = "non_existent_dialog.yaml";
  private static final String BLANK_STRING = "  ";

  private static final String PROP_FILE_NAME = "telegram.bot.dialog.file-name";
  private static final String PROP_BUTTONS_PER_ROW = "telegram.bot.dialog.buttons-per-row";

  private static final int BUTTONS_TOO_LOW = 0;
  private static final int BUTTONS_TOO_HIGH = 11;
  private static final int BUTTONS_VALID = 5;

  @Mock private ResourceLoader resourceLoader;
  @Mock private Resource existingResource;
  @Mock private Resource missingResource;

  private DialogPropertiesValidator validator;

  @BeforeEach
  void setUp() {
    lenient().when(existingResource.exists()).thenReturn(true);
    lenient().when(missingResource.exists()).thenReturn(false);
    lenient().when(resourceLoader.getResource(anyString())).thenReturn(missingResource);
  }

  @Test
  void findInvalidProperties_shouldIgnoreNullFileName() {
    DialogProperties props = new DialogProperties(null, BUTTONS_VALID);
    validator = new DialogPropertiesValidator(props, resourceLoader);

    List<String> errors = validator.findInvalidProperties();

    assertTrue(errors.stream().noneMatch(e -> e.contains(PROP_FILE_NAME)));
  }

  @Test
  void findInvalidProperties_shouldIgnoreBlankFileName() {
    DialogProperties props = new DialogProperties(BLANK_STRING, BUTTONS_VALID);
    validator = new DialogPropertiesValidator(props, resourceLoader);

    List<String> errors = validator.findInvalidProperties();

    assertTrue(errors.stream().noneMatch(e -> e.contains(PROP_FILE_NAME)));
  }

  @Test
  void findMissingProperties_shouldReturnFileNameError_whenFileNameIsNull() {
    DialogProperties props = new DialogProperties(null, BUTTONS_VALID);
    validator = new DialogPropertiesValidator(props, resourceLoader);

    assertTrue(validator.findMissingProperties().contains(PROP_FILE_NAME));
  }

  @Test
  void findMissingProperties_shouldReturnFileNameError_whenFileNameIsBlank() {
    DialogProperties props = new DialogProperties(BLANK_STRING, BUTTONS_VALID);
    validator = new DialogPropertiesValidator(props, resourceLoader);

    assertTrue(validator.findMissingProperties().contains(PROP_FILE_NAME));
  }

  @Test
  void findMissingProperties_shouldReturnEmpty_whenFileNameIsValid() {
    DialogProperties props = new DialogProperties(VALID_FILE_NAME, BUTTONS_VALID);
    validator = new DialogPropertiesValidator(props, resourceLoader);

    assertTrue(validator.findMissingProperties().isEmpty());
  }

  @Test
  void findInvalidProperties_shouldReturnError_whenFileDoesNotExist() {
    DialogProperties props = new DialogProperties(INVALID_FILE_NAME, BUTTONS_VALID);
    validator = new DialogPropertiesValidator(props, resourceLoader);

    when(resourceLoader.getResource(anyString())).thenReturn(missingResource);

    List<String> errors = validator.findInvalidProperties();

    assertTrue(errors.stream().anyMatch(e -> e.contains(PROP_FILE_NAME)));
    assertTrue(errors.stream().anyMatch(e -> e.contains("not found")));
  }

  @Test
  void findInvalidProperties_shouldNotReturnError_whenFileExists() {
    DialogProperties props = new DialogProperties(VALID_FILE_NAME, BUTTONS_VALID);
    validator = new DialogPropertiesValidator(props, resourceLoader);

    when(resourceLoader.getResource(anyString())).thenReturn(existingResource);

    List<String> errors = validator.findInvalidProperties();

    assertTrue(errors.stream().noneMatch(e -> e.contains(PROP_FILE_NAME)));
  }

  @Test
  void findInvalidProperties_shouldReturnError_whenButtonsPerRowIsTooLow() {
    DialogProperties props = new DialogProperties(VALID_FILE_NAME, BUTTONS_TOO_LOW);
    validator = new DialogPropertiesValidator(props, resourceLoader);

    when(resourceLoader.getResource(anyString())).thenReturn(existingResource);

    List<String> errors = validator.findInvalidProperties();

    assertTrue(errors.stream().anyMatch(e -> e.contains(PROP_BUTTONS_PER_ROW)));
    assertTrue(errors.stream().anyMatch(e -> e.contains("between")));
  }

  @Test
  void findInvalidProperties_shouldReturnError_whenButtonsPerRowIsTooHigh() {
    DialogProperties props = new DialogProperties(VALID_FILE_NAME, BUTTONS_TOO_HIGH);
    validator = new DialogPropertiesValidator(props, resourceLoader);

    when(resourceLoader.getResource(anyString())).thenReturn(existingResource);

    List<String> errors = validator.findInvalidProperties();

    assertTrue(errors.stream().anyMatch(e -> e.contains(PROP_BUTTONS_PER_ROW)));
    assertTrue(errors.stream().anyMatch(e -> e.contains("between")));
  }

  @Test
  void findInvalidProperties_shouldNotReturnError_whenButtonsPerRowIsValid() {
    DialogProperties props = new DialogProperties(VALID_FILE_NAME, BUTTONS_VALID);
    validator = new DialogPropertiesValidator(props, resourceLoader);

    when(resourceLoader.getResource(anyString())).thenReturn(existingResource);

    List<String> errors = validator.findInvalidProperties();

    assertTrue(errors.stream().noneMatch(e -> e.contains(PROP_BUTTONS_PER_ROW)));
  }

  @Test
  void findInvalidProperties_shouldReturnEmpty_whenAllPropertiesAreValid() {
    DialogProperties props = new DialogProperties(VALID_FILE_NAME, BUTTONS_VALID);
    validator = new DialogPropertiesValidator(props, resourceLoader);

    when(resourceLoader.getResource(anyString())).thenReturn(existingResource);

    assertTrue(validator.findInvalidProperties().isEmpty());
  }
}
