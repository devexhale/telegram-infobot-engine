package io.github.devexhale.botengine.parser.format;

import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import java.io.InputStream;

public interface ConfigFormatReader {

  boolean supports(String fileName);

  <T> T read(InputStream inputStream, TypeReference<T> typeReference) throws IOException;
}
