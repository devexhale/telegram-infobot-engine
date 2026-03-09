package com.github.jawisimo.botengine.parser;

import com.github.jawisimo.botengine.model.DialogMap;
import java.io.InputStream;

/**
 * Parses dialog configuration files into a {@link DialogMap}.
 *
 * <p>Implementations support specific file formats and are selected dynamically based on the dialog
 * file name.
 *
 * @since 1.0
 */
public interface DialogParser {

  /**
   * Determines whether this parser supports the given file.
   *
   * @param fileName the dialog configuration file name
   * @return {@code true} if the parser can handle the file format
   */
  boolean canParse(String fileName);

  /**
   * Parses the provided input stream into a dialog model.
   *
   * @param is the input stream containing dialog configuration data
   * @return the parsed {@link DialogMap}
   */
  DialogMap parse(InputStream is);
}
