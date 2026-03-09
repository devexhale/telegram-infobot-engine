package com.github.jawisimo.botengine.parser;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jawisimo.botengine.exception.DialogLoadingException;
import com.github.jawisimo.botengine.model.DialogMap;
import com.github.jawisimo.botengine.model.DialogNode;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * {@link DialogParser} implementation for JSON dialog definitions.
 *
 * <p>Parses {@code .json} files into a {@link DialogMap} using Jackson.
 *
 * @since 1.0
 */
@Component
public class JsonDialogParser implements DialogParser {

  private static final String FORMAT_JSON = ".json";

  private final ObjectMapper jsonMapper = new ObjectMapper();

  /** Returns {@code true} if the file has a {@code .json} extension. */
  @Override
  public boolean canParse(String fileName) {
    return fileName.endsWith(FORMAT_JSON);
  }

  /**
   * Parses a JSON dialog configuration into a {@link DialogMap}.
   *
   * @throws DialogLoadingException if parsing fails
   */
  @Override
  public DialogMap parse(InputStream is) {
    try {
      TypeReference<Map<String, DialogNode>> typeRef = new TypeReference<>() {};
      return new DialogMap(jsonMapper.readValue(is, typeRef));
    } catch (IOException e) {
      throw new DialogLoadingException("Failed to parse JSON dialog file", e);
    }
  }
}
