package io.github.jawisimo.botsengine.model;

/**
 * Represents a single content element within a dialog node.
 *
 * @param type the content type
 * @param text the text message for text content
 * @param media the media definition for media content
 * @since 1.0
 */
public record ContentNode(ContentType type, String text, Media media) {}
