package io.github.devexhale.botengine.parser.format;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.Set;
import org.springframework.stereotype.Component;

/**
 * Reads configuration files in JSON format.
 *
 * @since 1.0
 */
@Component
public class JsonFormatReader extends AbstractFormatReader {

  public JsonFormatReader() {
    super(new ObjectMapper().registerModule(new JavaTimeModule()), Set.of(".json"));
  }
}
