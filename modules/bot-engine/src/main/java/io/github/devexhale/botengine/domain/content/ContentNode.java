package io.github.devexhale.botengine.domain.content;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a single content element within a definition node.
 *
 * @param type the type of the content (e.g., text, photo, video)
 * @param text the text payload or file URL
 * @param fileName the name of the file to send for media types
 * @param caption the optional caption for media content
 * @since 1.0
 */
public record ContentNode(
    ContentType type, String text, @JsonProperty("file_name") String fileName, String caption) {}
