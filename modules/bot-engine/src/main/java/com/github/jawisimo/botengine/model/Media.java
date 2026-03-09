package com.github.jawisimo.botengine.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a media definition used in dialog content.
 *
 * @param type the media type (photo, video, audio, etc.)
 * @param fileName the media file name located in the media resources folder
 * @param caption the optional caption displayed with the media
 * @since 1.0
 */
public record Media(String type, @JsonProperty("file_name") String fileName, String caption) {}
