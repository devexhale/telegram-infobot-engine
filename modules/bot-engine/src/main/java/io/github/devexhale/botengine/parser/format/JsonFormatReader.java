package io.github.devexhale.botengine.parser.format;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Set;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class JsonFormatReader extends AbstractFormatReader {

  public JsonFormatReader(@Qualifier("jsonMapper") ObjectMapper objectMapper) {
    super(objectMapper, Set.of(".json"));
  }
}
