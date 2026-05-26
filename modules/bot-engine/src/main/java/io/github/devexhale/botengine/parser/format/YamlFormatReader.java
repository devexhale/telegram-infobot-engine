package io.github.devexhale.botengine.parser.format;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Set;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class YamlFormatReader extends AbstractFormatReader {

  public YamlFormatReader(@Qualifier("yamlMapper") ObjectMapper objectMapper) {
    super(objectMapper, Set.of(".yaml", ".yml"));
  }
}
