package io.github.devexhale.botengine.diagnostics.analyzer;

import io.github.devexhale.botengine.diagnostics.exception.DefinitionInitializationException;
import org.springframework.boot.diagnostics.FailureAnalysis;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DefinitionInitializationFailureAnalyzerTest {

  private final DefinitionInitializationFailureAnalyzer analyzer =
      new DefinitionInitializationFailureAnalyzer();

  @Test
  void analyze_shouldReturnCorrectFailureAnalysis_whenExceptionProvided() {
    String exceptionMsg = "Invalid syntax in file definitions.json";
    DefinitionInitializationException cause = new DefinitionInitializationException(exceptionMsg);

    FailureAnalysis analysis = analyzer.analyze(cause, cause);

    assertNotNull(analysis);
    assertEquals(
        "Definitions failed to load. Invalid syntax in file definitions.json",
        analysis.getDescription());
    assertEquals(
        "Fix your definition configuration files and restart the application.",
        analysis.getAction());
    assertEquals(cause, analysis.getCause());
  }
}
