package io.github.devexhale.botengine.parser.format;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class YamlFormatReaderTest {

  private static final String FILE_JSON = "dialog.json";
  private static final String FILE_NO_EXT = "dialog";

  @Mock private ObjectMapper objectMapper;

  @InjectMocks private YamlFormatReader reader;

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
  void read_shouldDelegateToObjectMapperAndReturnResult() throws IOException {
    InputStream inputStream =
        new ByteArrayInputStream("key: value".getBytes(StandardCharsets.UTF_8));
    TypeReference<Map<String, String>> typeRef = new TypeReference<>() {};
    Map<String, String> expected = Map.of("key", "value");

    when(objectMapper.readValue(
            any(InputStream.class), ArgumentMatchers.<TypeReference<Object>>any()))
        .thenReturn(expected);

    Map<String, String> result = reader.read(inputStream, typeRef);

    assertEquals(expected, result);
    verify(objectMapper).readValue(inputStream, typeRef);
  }
}
