package com.github.jawisimo.botengine.interaction.navigator.dto;

/**
 * Represents a resolved navigation request derived from user input.
 *
 * <p>Contains the raw input received from the user, the resolved dialog node key, and a flag
 * indicating whether the navigation originated from a button interaction.
 *
 * <p>The request is produced by the navigation resolver and later processed by the dialog routing
 * components.
 *
 * @since 1.0
 */
public record NavigationRequest(String rawInput, String nodeKey, boolean fromButton) {}
