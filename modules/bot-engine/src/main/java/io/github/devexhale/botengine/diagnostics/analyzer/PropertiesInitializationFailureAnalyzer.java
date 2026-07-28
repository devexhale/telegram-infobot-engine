package io.github.devexhale.botengine.diagnostics.analyzer;

import io.github.devexhale.botengine.diagnostics.exception.PropertiesInitializationException;
import lombok.NonNull;
import org.springframework.boot.diagnostics.AbstractFailureAnalyzer;
import org.springframework.boot.diagnostics.FailureAnalysis;

/**
 * Analyzes {@link PropertiesInitializationException} to provide user-friendly diagnostics when
 * application properties fail to load or validate.
 *
 * @since 1.0
 */
public class PropertiesInitializationFailureAnalyzer
    extends AbstractFailureAnalyzer<@NonNull PropertiesInitializationException> {

  @Override
  protected FailureAnalysis analyze(
      @NonNull Throwable rootFailure, PropertiesInitializationException cause) {
    String message = "Properties failed to load. " + cause.getMessage();
    String action = "Fix your properties and restart the application.";
    return new FailureAnalysis(message, action, cause);
  }
}
