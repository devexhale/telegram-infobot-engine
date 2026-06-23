package io.github.devexhale.botengine.parser;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.fasterxml.jackson.core.type.TypeReference;
import io.github.devexhale.botengine.diagnostics.exception.DefinitionInitializationException;
import io.github.devexhale.botengine.domain.dialog.DialogMap;
import io.github.devexhale.botengine.parser.definition.MapDefinitionReader;
import io.github.devexhale.botengine.parser.definition.MapDefinitionReaderRegistry;
import io.github.devexhale.botengine.parser.format.ConfigFormatReader;
import io.github.devexhale.botengine.testsupport.logger.TestLogCaptor;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MapParserTest {

  private static final String FILE_NAME = "dialog.yaml";
  private static final Class<DialogMap> TARGET_TYPE = DialogMap.class;

  @Mock private ConfigFormatReader formatReader;
  @Mock private MapDefinitionReader<DialogMap, Object> definitionReader;
  @Mock private MapDefinitionReaderRegistry registry;

  @Mock private Object rawData;
  @Mock private DialogMap mappedData;
  @Mock private InputStream inputStream;

  private final TypeReference<Object> rawType = new TypeReference<>() {};

  private MapParser mapParser;

  @BeforeEach
  void setUp() {
    mapParser = new MapParser(List.of(formatReader), registry);
  }

  @Test
  void parse_shouldReturnMappedResultAndLogSuccess_whenParsingIsValid() throws IOException {
    when(formatReader.supports(FILE_NAME)).thenReturn(true);
    when(registry.get(TARGET_TYPE)).thenReturn(definitionReader);
    when(definitionReader.rawType()).thenReturn(rawType);
    when(formatReader.read(inputStream, rawType)).thenReturn(rawData);
    when(definitionReader.map(rawData)).thenReturn(mappedData);

    try (TestLogCaptor logCaptor = new TestLogCaptor(MapParser.class)) {
      DialogMap result = mapParser.parse(FILE_NAME, inputStream, TARGET_TYPE);

      assertEquals(mappedData, result);

      ILoggingEvent logEvent = logCaptor.events().getFirst();
      assertEquals(Level.INFO, logEvent.getLevel());

      String message = logEvent.getFormattedMessage();

      assertTrue(message.contains("Parsed file"));
      assertTrue(message.contains(FILE_NAME));
      assertTrue(message.contains(TARGET_TYPE.getSimpleName()));
    }
  }

  @Test
  void parse_shouldThrowException_whenNoFormatReaderSupportsFile() {
    when(formatReader.supports(FILE_NAME)).thenReturn(false);

    DefinitionInitializationException exception =
        assertThrows(
            DefinitionInitializationException.class,
            () -> mapParser.parse(FILE_NAME, inputStream, TARGET_TYPE));

    assertTrue(exception.getMessage().contains(FILE_NAME));
  }

  @Test
  void parse_shouldThrowException_whenIOExceptionOccurs() throws IOException {
    IOException ioException = new IOException("Invalid syntax");

    when(formatReader.supports(FILE_NAME)).thenReturn(true);
    when(registry.get(TARGET_TYPE)).thenReturn(definitionReader);
    when(definitionReader.rawType()).thenReturn(rawType);
    when(formatReader.read(inputStream, rawType)).thenThrow(ioException);

    DefinitionInitializationException exception =
        assertThrows(
            DefinitionInitializationException.class,
            () -> mapParser.parse(FILE_NAME, inputStream, TARGET_TYPE));

    assertTrue(exception.getMessage().contains(FILE_NAME));
    assertTrue(exception.getMessage().contains(TARGET_TYPE.getSimpleName()));
    assertEquals(ioException, exception.getCause());
  }
}
