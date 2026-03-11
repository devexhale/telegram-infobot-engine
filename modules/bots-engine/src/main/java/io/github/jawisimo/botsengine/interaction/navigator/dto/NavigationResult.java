package io.github.jawisimo.botsengine.interaction.navigator.dto;

/**
 * Represents the result of a dialog navigation attempt.
 *
 * <p>SUCCESS indicates that the requested dialog node was successfully resolved and executed.
 *
 * <p>NODE_NOT_FOUND indicates that the requested dialog node does not exist in the dialog
 * configuration.
 *
 * <p>IRRELEVANT_INPUT indicates that the user input does not correspond to any navigation option in
 * the current dialog context.
 *
 * @since 1.0
 */
public enum NavigationResult {
  SUCCESS,
  NODE_NOT_FOUND,
  IRRELEVANT_INPUT
}
