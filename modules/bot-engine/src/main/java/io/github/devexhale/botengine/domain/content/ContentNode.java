package io.github.devexhale.botengine.domain.content;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a single content element within a dialog node.
 *
 * @since 1.0
 */
public record ContentNode(
    ContentType type, String text, @JsonProperty("file_name") String fileName, String caption) {}
