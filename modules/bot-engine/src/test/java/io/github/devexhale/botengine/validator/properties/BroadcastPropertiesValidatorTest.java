package io.github.devexhale.botengine.validator.properties;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import io.github.devexhale.botengine.properties.BroadcastProperties;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

@ExtendWith(MockitoExtension.class)
class BroadcastPropertiesValidatorTest {

  private static final String VALID_FILE_NAME = "broadcast.yaml";
  private static final String INVALID_FILE_NAME = "non_existent_broadcast.yaml";
  private static final String VALID_TIMEZONE = "Europe/Kiev";
  private static final String INVALID_TIMEZONE = "Invalid/Timezone";
  private static final String BLANK_STRING = "  ";

  private static final String PROP_FILE_NAME = "telegram.bot.broadcast.file-name";
  private static final String PROP_TIMEZONE = "telegram.bot.broadcast.timezone";

  @Mock private ResourceLoader resourceLoader;
  @Mock private Resource existingResource;
  @Mock private Resource missingResource;

  private BroadcastPropertiesValidator validator;

  @BeforeEach
  void setUp() {
    lenient().when(existingResource.exists()).thenReturn(true);
    lenient().when(missingResource.exists()).thenReturn(false);
  }

  @Test
  void findInvalidProperties_shouldIgnoreNullFileName() {
    BroadcastProperties props = new BroadcastProperties(true, null, VALID_TIMEZONE);

    validator = new BroadcastPropertiesValidator(props, resourceLoader);

    List<String> errors = validator.findInvalidProperties();

    assertTrue(errors.stream().noneMatch(e -> e.contains(PROP_FILE_NAME)));
  }

  @Test
  void findMissingProperties_shouldReturnFileNameError_whenBroadcastEnabledAndFileNameIsNull() {
    BroadcastProperties props = new BroadcastProperties(true, null, VALID_TIMEZONE);
    validator = new BroadcastPropertiesValidator(props, resourceLoader);

    assertTrue(validator.findMissingProperties().contains(PROP_FILE_NAME));
  }

  @Test
  void findMissingProperties_shouldReturnFileNameError_whenBroadcastEnabledAndFileNameIsBlank() {
    BroadcastProperties props = new BroadcastProperties(true, BLANK_STRING, VALID_TIMEZONE);
    validator = new BroadcastPropertiesValidator(props, resourceLoader);

    assertTrue(validator.findMissingProperties().contains(PROP_FILE_NAME));
  }

  @Test
  void findMissingProperties_shouldNotReturnFileNameError_whenBroadcastDisabledAndFileNameIsNull() {
    BroadcastProperties props = new BroadcastProperties(false, null, VALID_TIMEZONE);
    validator = new BroadcastPropertiesValidator(props, resourceLoader);

    assertTrue(
        validator.findMissingProperties().stream().noneMatch(p -> p.contains(PROP_FILE_NAME)));
  }

  @Test
  void findMissingProperties_shouldReturnTimezoneError_whenTimezoneIsNull() {
    BroadcastProperties props = new BroadcastProperties(true, VALID_FILE_NAME, null);
    validator = new BroadcastPropertiesValidator(props, resourceLoader);

    assertTrue(validator.findMissingProperties().contains(PROP_TIMEZONE));
  }

  @Test
  void findMissingProperties_shouldReturnTimezoneError_whenTimezoneIsBlank() {
    BroadcastProperties props = new BroadcastProperties(true, VALID_FILE_NAME, BLANK_STRING);
    validator = new BroadcastPropertiesValidator(props, resourceLoader);

    assertTrue(validator.findMissingProperties().contains(PROP_TIMEZONE));
  }

  @Test
  void findMissingProperties_shouldReturnEmpty_whenAllRequiredPropertiesArePresent() {
    BroadcastProperties props = new BroadcastProperties(true, VALID_FILE_NAME, VALID_TIMEZONE);
    validator = new BroadcastPropertiesValidator(props, resourceLoader);

    assertTrue(validator.findMissingProperties().isEmpty());
  }

  @Test
  void findInvalidProperties_shouldReturnError_whenBroadcastEnabledAndFileNotFound() {
    BroadcastProperties props = new BroadcastProperties(true, INVALID_FILE_NAME, VALID_TIMEZONE);
    validator = new BroadcastPropertiesValidator(props, resourceLoader);

    when(resourceLoader.getResource(anyString())).thenReturn(missingResource);

    List<String> errors = validator.findInvalidProperties();

    assertTrue(errors.stream().anyMatch(e -> e.contains(PROP_FILE_NAME)));
    assertTrue(errors.stream().anyMatch(e -> e.contains("not found")));
  }

  @Test
  void findInvalidProperties_shouldNotReturnError_whenBroadcastEnabledAndFileExists() {
    BroadcastProperties props = new BroadcastProperties(true, VALID_FILE_NAME, VALID_TIMEZONE);
    validator = new BroadcastPropertiesValidator(props, resourceLoader);

    when(resourceLoader.getResource(anyString())).thenReturn(existingResource);

    List<String> errors = validator.findInvalidProperties();

    assertTrue(errors.stream().noneMatch(e -> e.contains(PROP_FILE_NAME)));
  }

  @Test
  void findInvalidProperties_shouldNotReturnFileError_whenBroadcastDisabledAndFileNotFound() {
    BroadcastProperties props = new BroadcastProperties(false, INVALID_FILE_NAME, VALID_TIMEZONE);
    validator = new BroadcastPropertiesValidator(props, resourceLoader);

    List<String> errors = validator.findInvalidProperties();

    assertTrue(errors.stream().noneMatch(e -> e.contains(PROP_FILE_NAME)));
  }

  @Test
  void findInvalidProperties_shouldReturnError_whenTimezoneIsInvalid() {
    BroadcastProperties props = new BroadcastProperties(true, VALID_FILE_NAME, INVALID_TIMEZONE);

    validator = new BroadcastPropertiesValidator(props, resourceLoader);

    when(resourceLoader.getResource(anyString())).thenReturn(existingResource);

    List<String> errors = validator.findInvalidProperties();

    assertTrue(errors.stream().anyMatch(e -> e.contains(PROP_TIMEZONE)));
    assertTrue(errors.stream().anyMatch(e -> e.contains("invalid value")));
  }

  @Test
  void findInvalidProperties_shouldNotReturnError_whenTimezoneIsValid() {
    BroadcastProperties props = new BroadcastProperties(true, VALID_FILE_NAME, VALID_TIMEZONE);
    validator = new BroadcastPropertiesValidator(props, resourceLoader);

    when(resourceLoader.getResource(anyString())).thenReturn(existingResource);

    List<String> errors = validator.findInvalidProperties();

    assertTrue(errors.stream().noneMatch(e -> e.contains(PROP_TIMEZONE)));
  }

  @Test
  void findInvalidProperties_shouldIgnoreNullTimezone() {
    BroadcastProperties props = new BroadcastProperties(true, VALID_FILE_NAME, null);
    validator = new BroadcastPropertiesValidator(props, resourceLoader);

    when(resourceLoader.getResource(anyString())).thenReturn(existingResource);

    List<String> errors = validator.findInvalidProperties();

    assertTrue(errors.stream().noneMatch(e -> e.contains(PROP_TIMEZONE)));
  }

  @Test
  void findInvalidProperties_shouldReturnEmpty_whenAllPropertiesAreValid() {
    BroadcastProperties props = new BroadcastProperties(true, VALID_FILE_NAME, VALID_TIMEZONE);
    validator = new BroadcastPropertiesValidator(props, resourceLoader);

    when(resourceLoader.getResource(anyString())).thenReturn(existingResource);

    assertTrue(validator.findInvalidProperties().isEmpty());
  }

  @Test
  void findInvalidProperties_shouldIgnoreBlankTimezone() {
    BroadcastProperties props = new BroadcastProperties(true, VALID_FILE_NAME, BLANK_STRING);

    validator = new BroadcastPropertiesValidator(props, resourceLoader);

    when(resourceLoader.getResource(anyString())).thenReturn(existingResource);

    List<String> errors = validator.findInvalidProperties();

    assertTrue(errors.stream().noneMatch(e -> e.contains(PROP_TIMEZONE)));
  }

  @Test
  void findInvalidProperties_shouldIgnoreBlankFileName() {
    BroadcastProperties props = new BroadcastProperties(true, BLANK_STRING, VALID_TIMEZONE);

    validator = new BroadcastPropertiesValidator(props, resourceLoader);

    List<String> errors = validator.findInvalidProperties();

    assertTrue(errors.stream().noneMatch(e -> e.contains(PROP_FILE_NAME)));
  }
}
