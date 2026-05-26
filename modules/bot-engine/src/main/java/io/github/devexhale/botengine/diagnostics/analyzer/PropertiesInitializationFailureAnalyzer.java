package io.github.devexhale.botengine.diagnostics.analyzer;

import io.github.devexhale.botengine.diagnostics.exception.PropertiesInitializationException;
import org.springframework.boot.diagnostics.AbstractFailureAnalyzer;
import org.springframework.boot.diagnostics.FailureAnalysis;

public class PropertiesInitializationFailureAnalyzer
    extends AbstractFailureAnalyzer<PropertiesInitializationException> {

  @Override
  protected FailureAnalysis analyze(
      Throwable rootFailure, PropertiesInitializationException cause) {
    String message = "Properties failed to load. " + cause.getMessage();
    String action = "Fix your properties and restart the application.";
    return new FailureAnalysis(message, action, cause);
  }
}
