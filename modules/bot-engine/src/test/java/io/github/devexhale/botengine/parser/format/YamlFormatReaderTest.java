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

class YamlFormatReaderTest {

  private static final String FILE_JSON = "dialog.json";
  private static final String FILE_NO_EXT = "dialog";

  private final YamlFormatReader reader = new YamlFormatReader();

  @ParameterizedTest
  @ValueSource(
      strings = {"dialog.yaml", "dialog.yml", "DIALOG.YAML", "DIALOG.YML", "path/to/dialog.yaml"})
  void supports_shouldReturnTrue_whenFileNameEndsWithYamlOrYml(String fileName) {
    assertTrue(reader.supports(fileName));
  }

  @Test
  void supports_shouldReturnFalse_whenFileNameEndsWithJson() {
    assertFalse(reader.supports(FILE_JSON));
  }

  @Test
  void supports_shouldReturnFalse_whenFileNameHasNoExtension() {
    assertFalse(reader.supports(FILE_NO_EXT));
  }

  @Test
  void read_shouldDeserializeYaml() throws IOException {
    InputStream inputStream =
        new ByteArrayInputStream(
            """
            key: value
            """
                .getBytes(StandardCharsets.UTF_8));

    TypeReference<Map<String, String>> typeRef = new TypeReference<>() {};

    Map<String, String> result = reader.read(inputStream, typeRef);

    assertEquals(Map.of("key", "value"), result);
  }
}
