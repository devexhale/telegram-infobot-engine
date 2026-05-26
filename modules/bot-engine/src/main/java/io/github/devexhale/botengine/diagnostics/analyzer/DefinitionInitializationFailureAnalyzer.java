package io.github.devexhale.botengine.diagnostics.analyzer;

import io.github.devexhale.botengine.diagnostics.exception.DefinitionInitializationException;
import org.springframework.boot.diagnostics.AbstractFailureAnalyzer;
import org.springframework.boot.diagnostics.FailureAnalysis;

public class DefinitionInitializationFailureAnalyzer
    extends AbstractFailureAnalyzer<DefinitionInitializationException> {

  @Override
  protected FailureAnalysis analyze(
      Throwable rootFailure, DefinitionInitializationException cause) {
    String message = "Definitions failed to load. " + cause.getMessage();
    String action = "Fix your definition configuration files and restart the application.";
    return new FailureAnalysis(message, action, cause);
  }
}
