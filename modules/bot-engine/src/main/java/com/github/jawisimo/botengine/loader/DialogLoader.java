package com.github.jawisimo.botengine.loader;

import com.github.jawisimo.botengine.exception.DialogLoadingException;
import com.github.jawisimo.botengine.model.DialogMap;
import com.github.jawisimo.botengine.parser.DialogParser;
import com.github.jawisimo.botengine.parser.DialogParserProvider;
import com.github.jawisimo.botengine.validator.DialogValidator;
import java.io.InputStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Loads dialog definitions from a classpath configuration file.
 *
 * <p>Selects a suitable {@link DialogParser} using {@link DialogParserProvider}.
 *
 * <p>The parsed {@link DialogMap} is validated by {@link DialogValidator}.
 *
 * <p>If parsing or validation fails, a {@link DialogLoadingException} is thrown.
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
   * Loads and validates a dialog definition file.
   *
   * <p>The file is resolved from the classpath and parsed using a {@link DialogParser}.
   *
   * <p>The resulting {@link DialogMap} is validated before being returned.
   *
   * @param dialogFileName the dialog configuration file name
   * @return the parsed and validated dialog map
   * @throws DialogLoadingException if the file cannot be found, parsed, or validated
   */
  public DialogMap load(String dialogFileName) {
    try (InputStream is = getClass().getClassLoader().getResourceAsStream(dialogFileName)) {
      if (is == null) {
        throw new DialogLoadingException("Dialog file not found: '%s'".formatted(dialogFileName));
      }

      DialogParser parser = dialogParserProvider.getParser(dialogFileName);
      DialogMap dialogMap = parser.parse(is);
      dialogValidator.validate(dialogMap, dialogFileName);
      return dialogMap;
    } catch (DialogLoadingException e) {
      throw e;
    } catch (Exception e) {
      throw new DialogLoadingException(
          "Failed to load dialog file: '%s'".formatted(dialogFileName), e);
    }
  }
}
