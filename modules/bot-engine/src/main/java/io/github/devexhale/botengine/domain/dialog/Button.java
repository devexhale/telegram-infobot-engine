package io.github.devexhale.botengine.domain.dialog;

/**
 * Represents a dialog button.
 *
 * @param label the button label shown to the user
 * @param next the target node key for navigation
 * @param url the external URL opened by the button
 * @since 1.0
 */
public record Button(String label, String next, String url) {}
