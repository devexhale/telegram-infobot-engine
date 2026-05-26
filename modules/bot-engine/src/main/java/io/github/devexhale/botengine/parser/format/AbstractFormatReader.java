package io.github.devexhale.botengine.parser.format;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Set;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractFormatReader implements ConfigFormatReader {

  private final ObjectMapper objectMapper;
  private final Set<String> supportedExtensions;

  @Override
  public boolean supports(String fileName) {
    String normalized = fileName.toLowerCase(Locale.ROOT);
    return supportedExtensions.stream().anyMatch(normalized::endsWith);
  }

  @Override
  public <T> T read(InputStream inputStream, TypeReference<T> typeReference) throws IOException {
    return objectMapper.readValue(inputStream, typeReference);
  }
}
