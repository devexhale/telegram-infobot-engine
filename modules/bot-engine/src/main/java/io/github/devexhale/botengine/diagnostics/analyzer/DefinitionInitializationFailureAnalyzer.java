package io.github.devexhale.botengine.diagnostics.analyzer;

import io.github.devexhale.botengine.diagnostics.exception.DefinitionInitializationException;
import lombok.NonNull;
import org.springframework.boot.diagnostics.AbstractFailureAnalyzer;
import org.springframework.boot.diagnostics.FailureAnalysis;

/**
 * Analyzes {@link DefinitionInitializationException} to provide user-friendly diagnostics when
 * definition configuration files fail to load.
 *
 * @since 1.0
 */
public class DefinitionInitializationFailureAnalyzer
    extends AbstractFailureAnalyzer<@NonNull DefinitionInitializationException> {

  @Override
  protected FailureAnalysis analyze(
      @NonNull Throwable rootFailure, DefinitionInitializationException cause) {
    String message = "Definitions failed to load. " + cause.getMessage();
    String action = "Fix your definition configuration files and restart the application.";
    return new FailureAnalysis(message, action, cause);
  }
}
