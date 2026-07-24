package io.github.devexhale.botengine.parser.format;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
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
        YAMLMapper.builder()
            .addModule(new JavaTimeModule())
            .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
            .build(),
        Set.of(".yaml", ".yml"));
  }
}
