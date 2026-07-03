package io.github.devexhale.botengine.parser.format;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.Set;
import org.springframework.stereotype.Component;

/**
 * Reads configuration files in YAML format.
 *
 * @since 1.0
 */
@Component
public class YamlFormatReader extends AbstractFormatReader {

  public YamlFormatReader() {
    super(
        new ObjectMapper(new YAMLFactory()).registerModule(new JavaTimeModule()),
        Set.of(".yaml", ".yml"));
  }
}
