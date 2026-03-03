package com.github.jawisimo.botengine.interaction.node.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.jawisimo.botengine.exception.DialogLoadingException;
import com.github.jawisimo.botengine.validator.ValidationErrorFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents media content attached to a dialog node.
 *
 * <p>Defines media type, file name, and optional caption.
 *
 * @param type media type (e.g. photo, video, document)
 * @param fileName name of the media file to send
 * @param caption optional caption displayed below the media
 * @since 1.0
 */
public record Media(String type, @JsonProperty("file_name") String fileName, String caption) {

  /**
   * Validates required media fields.
   *
   * @throws DialogLoadingException if required media properties are missing
   */
  public Media {
    List<String> errors = new ArrayList<>();

    if (type == null || type.isBlank()) {
      errors.add("Media type is missing or blank");
    }

    if (fileName == null || fileName.isBlank()) {
      errors.add("Media file name is missing or blank");
    }

    if (!errors.isEmpty()) {
      String errorMessage = ValidationErrorFormatter.format("Media loading failed", errors);
      throw new DialogLoadingException(errorMessage);
    }
  }
}
