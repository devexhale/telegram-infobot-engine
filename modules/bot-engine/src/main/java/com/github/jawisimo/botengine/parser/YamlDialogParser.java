package com.github.jawisimo.botengine.parser;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.github.jawisimo.botengine.exception.DialogLoadingException;
import com.github.jawisimo.botengine.interaction.node.model.DialogMap;
import com.github.jawisimo.botengine.interaction.node.model.DialogNode;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * {@link DialogParser} implementation for YAML dialog definitions.
 *
 * <p>Parses {@code .yaml} and {@code .yml} files into a {@link DialogMap} using Jackson.
 *
 * @since 1.0
 */
@Component
public class YamlDialogParser implements DialogParser {

  private static final String FORMAT_YAML = ".yaml";
  private static final String FORMAT_YML = ".yml";

  private final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());

  /** Returns {@code true} if the file has a {@code .yaml} or {@code .yml} extension. */
  @Override
  public boolean canParse(String fileName) {
    return fileName.endsWith(FORMAT_YAML) || fileName.endsWith(FORMAT_YML);
  }

  /**
   * Parses a YAML dialog configuration into a {@link DialogMap}.
   *
   * @throws DialogLoadingException if parsing fails
   */
  @Override
  public DialogMap parse(InputStream is) {
    try {
      TypeReference<Map<String, DialogNode>> typeRef = new TypeReference<>() {};
      return new DialogMap(yamlMapper.readValue(is, typeRef));
    } catch (IOException e) {
      throw new DialogLoadingException("Failed to parse YAML dialog", e);
    }
  }
}
