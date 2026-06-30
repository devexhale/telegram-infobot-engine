package io.github.devexhale.botengine.parser.format;

import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import java.io.InputStream;

/**
 * Strategy interface for reading configuration files in different formats.
 *
 * @since 1.0
 */
public interface ConfigFormatReader {

  /**
   * Checks whether this reader supports the given file format.
   *
   * @param fileName the name of the file to check
   * @return {@code true} if the format is supported, {@code false} otherwise
   */
  boolean supports(String fileName);

  /**
   * Reads the input stream and parses it into the specified target type.
   *
   * @param inputStream the input stream to read
   * @param typeReference the type reference for deserialization
   * @param <T> the target type
   * @return the parsed object
   * @throws IOException if an I/O error occurs during reading
   */
  <T> T read(InputStream inputStream, TypeReference<T> typeReference) throws IOException;
}
