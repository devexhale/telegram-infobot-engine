package io.github.devexhale.botengine.diagnostics.analyzer;

import io.github.devexhale.botengine.diagnostics.exception.PropertiesInitializationException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.diagnostics.FailureAnalysis;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PropertiesInitializationFailureAnalyzerTest {

  private final PropertiesInitializationFailureAnalyzer analyzer =
      new PropertiesInitializationFailureAnalyzer();

  @Test
  void analyze_shouldReturnCorrectFailureAnalysis_whenExceptionProvided() {
    String exceptionMsg = "Missing required bot token property";
    PropertiesInitializationException cause = new PropertiesInitializationException(exceptionMsg);

    FailureAnalysis analysis = analyzer.analyze(cause, cause);

    assertNotNull(analysis);
    assertEquals(
        "Properties failed to load. Missing required bot token property",
        analysis.getDescription());
    assertEquals("Fix your properties and restart the application.", analysis.getAction());
    assertEquals(cause, analysis.getCause());
  }
}
