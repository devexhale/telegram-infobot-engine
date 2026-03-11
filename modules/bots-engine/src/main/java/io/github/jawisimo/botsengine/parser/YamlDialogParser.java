package io.github.jawisimo.botsengine.parser;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.github.jawisimo.botsengine.model.DialogMap;
import io.github.jawisimo.botsengine.model.DialogNode;
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
  public boolean supports(String dialogFileName) {
    return dialogFileName.endsWith(FORMAT_YAML) || dialogFileName.endsWith(FORMAT_YML);
  }

  /**
   * Parses a YAML dialog configuration into a {@link DialogMap}.
   *
   * @param is the YAML input stream
   * @return the parsed dialog map
   * @throws IOException if parsing fails
   */
  @Override
  public DialogMap parse(InputStream is) throws IOException {
    TypeReference<Map<String, DialogNode>> typeRef = new TypeReference<>() {};
    return new DialogMap(yamlMapper.readValue(is, typeRef));
  }
}
