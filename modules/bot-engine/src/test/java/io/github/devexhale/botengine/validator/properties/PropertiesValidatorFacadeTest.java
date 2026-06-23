package io.github.devexhale.botengine.validator.properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import io.github.devexhale.botengine.diagnostics.exception.PropertiesInitializationException;
import io.github.devexhale.botengine.testsupport.logger.TestLogCaptor;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PropertiesValidatorFacadeTest {

  private static final String MISSING_PROP_1 = "prop.missing.one";
  private static final String MISSING_PROP_2 = "prop.missing.two";
  private static final String INVALID_PROP_1 = "prop.invalid.one: must be positive";
  private static final String INVALID_PROP_2 = "prop.invalid.two: invalid format";

  @Mock private PropertiesValidator validatorOne;
  @Mock private PropertiesValidator validatorTwo;

  @Test
  void validateAll_shouldLogSuccess_whenAllValidatorsReturnEmpty() {
    try (TestLogCaptor logCaptor = new TestLogCaptor(PropertiesValidatorFacade.class)) {
      when(validatorOne.findMissingProperties()).thenReturn(List.of());
      when(validatorOne.findInvalidProperties()).thenReturn(List.of());
      when(validatorTwo.findMissingProperties()).thenReturn(List.of());
      when(validatorTwo.findInvalidProperties()).thenReturn(List.of());

      PropertiesValidatorFacade facade =
          new PropertiesValidatorFacade(List.of(validatorOne, validatorTwo));

      assertDoesNotThrow(facade::validateAll);

      ILoggingEvent successLog =
          logCaptor.events().stream()
              .filter(e -> e.getFormattedMessage().contains("validated successfully"))
              .findFirst()
              .orElseThrow();
      assertSame(Level.INFO, successLog.getLevel());
    }
  }

  @Test
  void validateAll_shouldThrowException_whenMissingPropertiesFound() {
    when(validatorOne.findMissingProperties()).thenReturn(List.of(MISSING_PROP_1));
    when(validatorOne.findInvalidProperties()).thenReturn(List.of());
    when(validatorTwo.findMissingProperties()).thenReturn(List.of(MISSING_PROP_2));
    when(validatorTwo.findInvalidProperties()).thenReturn(List.of());

    PropertiesValidatorFacade facade =
        new PropertiesValidatorFacade(List.of(validatorOne, validatorTwo));

    PropertiesInitializationException exception =
        assertThrows(PropertiesInitializationException.class, facade::validateAll);

    String message = exception.getMessage();
    assertTrue(message.contains("Required properties are missing"));
    assertTrue(message.contains(MISSING_PROP_1));
    assertTrue(message.contains(MISSING_PROP_2));
  }

  @Test
  void validateAll_shouldThrowException_whenInvalidPropertiesFound() {
    when(validatorOne.findMissingProperties()).thenReturn(List.of());
    when(validatorOne.findInvalidProperties()).thenReturn(List.of(INVALID_PROP_1));
    when(validatorTwo.findMissingProperties()).thenReturn(List.of());
    when(validatorTwo.findInvalidProperties()).thenReturn(List.of(INVALID_PROP_2));

    PropertiesValidatorFacade facade =
        new PropertiesValidatorFacade(List.of(validatorOne, validatorTwo));

    PropertiesInitializationException exception =
        assertThrows(PropertiesInitializationException.class, facade::validateAll);

    String message = exception.getMessage();
    assertTrue(message.contains("Invalid properties"));
    assertTrue(message.contains(INVALID_PROP_1));
    assertTrue(message.contains(INVALID_PROP_2));
  }

  @Test
  void validateAll_shouldThrowException_whenBothMissingAndInvalidPropertiesFound() {
    when(validatorOne.findMissingProperties()).thenReturn(List.of(MISSING_PROP_1));
    when(validatorOne.findInvalidProperties()).thenReturn(List.of(INVALID_PROP_1));
    when(validatorTwo.findMissingProperties()).thenReturn(List.of());
    when(validatorTwo.findInvalidProperties()).thenReturn(List.of());

    PropertiesValidatorFacade facade =
        new PropertiesValidatorFacade(List.of(validatorOne, validatorTwo));

    PropertiesInitializationException exception =
        assertThrows(PropertiesInitializationException.class, facade::validateAll);

    String message = exception.getMessage();
    assertTrue(message.contains("Required properties are missing"));
    assertTrue(message.contains("Invalid properties"));
    assertTrue(message.contains(MISSING_PROP_1));
    assertTrue(message.contains(INVALID_PROP_1));
  }

  @Test
  void validateAll_shouldLogInitialization_whenStartingValidation() {
    try (TestLogCaptor logCaptor = new TestLogCaptor(PropertiesValidatorFacade.class)) {
      when(validatorOne.findMissingProperties()).thenReturn(List.of());
      when(validatorOne.findInvalidProperties()).thenReturn(List.of());
      when(validatorTwo.findMissingProperties()).thenReturn(List.of());
      when(validatorTwo.findInvalidProperties()).thenReturn(List.of());

      PropertiesValidatorFacade facade =
          new PropertiesValidatorFacade(List.of(validatorOne, validatorTwo));

      facade.validateAll();

      ILoggingEvent initLog =
          logCaptor.events().stream()
              .filter(e -> e.getFormattedMessage().contains("Validating configuration properties"))
              .findFirst()
              .orElseThrow();
      assertSame(Level.INFO, initLog.getLevel());
    }
  }
}
