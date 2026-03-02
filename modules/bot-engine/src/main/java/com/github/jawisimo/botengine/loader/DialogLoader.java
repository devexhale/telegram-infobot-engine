package com.github.jawisimo.botengine.loader;

import com.github.jawisimo.botengine.exception.DialogLoadingException;
import com.github.jawisimo.botengine.interaction.node.model.DialogMap;
import com.github.jawisimo.botengine.parser.DialogParser;
import com.github.jawisimo.botengine.parser.DialogParserProvider;
import com.github.jawisimo.botengine.validator.DialogValidator;
import java.io.InputStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Loads and validates dialog definitions from a classpath dialog configuration file.
 *
 * <p>Delegates parsing to {@link DialogParser} selected by {@link DialogParserProvider} and
 * validates the resulting {@link DialogMap} using {@link DialogValidator}.
 *
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DialogLoader {

  private final DialogValidator dialogValidator;
  private final DialogParserProvider dialogParserProvider;

  /**
   * Loads a dialog definition from the given classpath resource.
   *
   * @param dialogFileName the dialog configuration file name
   * @return the parsed and validated {@link DialogMap}
   * @throws DialogLoadingException if the resource cannot be found, parsed, or validated
   */
  public DialogMap load(String dialogFileName) {
    try (InputStream is = getClass().getClassLoader().getResourceAsStream(dialogFileName)) {
      if (is == null) {
        throw new DialogLoadingException("Dialog file not found: " + dialogFileName);
      }

      DialogParser parser = dialogParserProvider.getParser(dialogFileName);
      DialogMap dialogMap = parser.parse(is);
      dialogValidator.validateStartNode(dialogMap, dialogFileName);
      return dialogMap;
    } catch (Exception e) {
      throw new DialogLoadingException("Failed to load dialog file: " + dialogFileName, e);
    }
  }
}
