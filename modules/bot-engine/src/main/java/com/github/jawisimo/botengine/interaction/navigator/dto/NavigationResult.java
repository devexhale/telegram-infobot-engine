package com.github.jawisimo.botengine.interaction.navigator.dto;

/**
 * Represents the result of a dialog navigation attempt.
 *
 * <p>Indicates whether the requested dialog node was successfully executed or whether the
 * navigation failed due to configuration issues or irrelevant input.
 *
 * @since 1.0
 */
public enum NavigationResult {
  SUCCESS,
  NODE_NOTE_FOUND,
  IRRELEVANT_INPUT
}
