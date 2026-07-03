package io.github.devexhale.botengine.parser.format;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.core.type.TypeReference;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class JsonFormatReaderTest {

  private static final String FILE_YAML = "dialog.yaml";
  private static final String FILE_NO_EXT = "dialog";

  private final JsonFormatReader reader = new JsonFormatReader();

  @ParameterizedTest
  @ValueSource(strings = {"dialog.json", "DIALOG.JSON", "path/to/dialog.json", "config.JSON"})
  void supports_shouldReturnTrue_whenFileNameEndsWithJson(String fileName) {
    assertTrue(reader.supports(fileName));
  }

  @Test
  void supports_shouldReturnFalse_whenFileNameEndsWithYaml() {
    assertFalse(reader.supports(FILE_YAML));
  }

  @Test
  void supports_shouldReturnFalse_whenFileNameHasNoExtension() {
    assertFalse(reader.supports(FILE_NO_EXT));
  }

  @Test
  void read_shouldDeserializeJson() throws IOException {
    InputStream inputStream =
        new ByteArrayInputStream(
            """
            {
              "key": "value"
            }
            """
                .getBytes(StandardCharsets.UTF_8));

    TypeReference<Map<String, String>> typeRef = new TypeReference<>() {};

    Map<String, String> result = reader.read(inputStream, typeRef);

    assertEquals(Map.of("key", "value"), result);
  }
}
