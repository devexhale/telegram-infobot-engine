package io.github.devexhale.botengine.validator.definition;

import io.github.devexhale.botengine.diagnostics.exception.DefinitionInitializationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class DefinitionValidatorRegistryTest {

  private static final Class<String> STRING_TYPE = String.class;
  private static final Class<Integer> INTEGER_TYPE = Integer.class;
  private static final String DUPLICATE_ERROR_PART =
      "Duplicate definition validators found for type:";
  private static final String MISSING_ERROR_PART = "No definition validator registered for type:";

  @Mock private DefinitionValidator<String> stringValidator;
  @Mock private DefinitionValidator<Integer> intValidator;
  @Mock private DefinitionValidator<String> duplicateStringValidator;

  @BeforeEach
  void setUp() {
    lenient().when(stringValidator.targetType()).thenReturn(STRING_TYPE);
    lenient().when(intValidator.targetType()).thenReturn(INTEGER_TYPE);
    lenient().when(duplicateStringValidator.targetType()).thenReturn(STRING_TYPE);
  }

  @Test
  void constructor_shouldRegisterValidatorsSuccessfully() {
    DefinitionValidatorRegistry registry =
        new DefinitionValidatorRegistry(List.of(stringValidator, intValidator));

    DefinitionValidator<String> resultString = registry.get(STRING_TYPE);
    DefinitionValidator<Integer> resultInt = registry.get(INTEGER_TYPE);

    assertEquals(stringValidator, resultString);
    assertEquals(intValidator, resultInt);
  }

  @Test
  void constructor_shouldThrowIllegalStateException_whenDuplicateValidatorsExist() {
    List<DefinitionValidator<?>> validators = List.of(stringValidator, duplicateStringValidator);

    IllegalStateException exception =
        assertThrows(
            IllegalStateException.class, () -> new DefinitionValidatorRegistry(validators));

    assertTrue(exception.getMessage().contains(DUPLICATE_ERROR_PART));
    assertTrue(exception.getMessage().contains(STRING_TYPE.getSimpleName()));
  }

  @Test
  void get_shouldReturnRegisteredValidator() {
    DefinitionValidatorRegistry registry =
        new DefinitionValidatorRegistry(List.of(stringValidator));

    DefinitionValidator<String> result = registry.get(STRING_TYPE);

    assertEquals(stringValidator, result);
  }

  @Test
  void get_shouldThrowException_whenValidatorNotFound() {
    DefinitionValidatorRegistry registry =
        new DefinitionValidatorRegistry(List.of(stringValidator));

    DefinitionInitializationException exception =
        assertThrows(DefinitionInitializationException.class, () -> registry.get(INTEGER_TYPE));

    assertTrue(exception.getMessage().contains(MISSING_ERROR_PART));
    assertTrue(exception.getMessage().contains(INTEGER_TYPE.getSimpleName()));
  }
}
