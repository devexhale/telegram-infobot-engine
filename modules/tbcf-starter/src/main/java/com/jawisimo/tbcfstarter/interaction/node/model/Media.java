package com.jawisimo.tbcfstarter.interaction.node.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jawisimo.tbcfstarter.exception.DialogLoadingException;
import com.jawisimo.tbcfstarter.validator.ValidationErrorFormatter;

import java.util.ArrayList;
import java.util.List;

public record Media(String type, @JsonProperty("file_name") String fileName, String caption) {

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
