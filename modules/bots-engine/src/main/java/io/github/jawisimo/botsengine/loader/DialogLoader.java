package io.github.jawisimo.botsengine.loader;

import io.github.jawisimo.botsengine.exception.DialogLoadingException;
import io.github.jawisimo.botsengine.model.DialogMap;
import io.github.jawisimo.botsengine.parser.DialogParser;
import io.github.jawisimo.botsengine.parser.DialogParserProvider;
import io.github.jawisimo.botsengine.validator.DialogValidator;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
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
 * <p>If loading, parsing, or validation fails, a {@link DialogLoadingException} is thrown.
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
   * <p>The file is resolved from the classpath, checked for empty content, and parsed using a
   * suitable {@link DialogParser}.
   *
   * <p>The resulting {@link DialogMap} is validated before being returned.
   *
   * @param dialogFileName the dialog configuration file name
   * @return the parsed and validated dialog map
   * @throws DialogLoadingException if the file cannot be found, is empty, cannot be parsed, or
   *     fails validation
   */
  public DialogMap load(String dialogFileName) {
    try (InputStream is = getClass().getClassLoader().getResourceAsStream(dialogFileName)) {
      if (is == null) {
        throw new DialogLoadingException("Dialog file not found: '%s'".formatted(dialogFileName));
      }

      byte[] content = is.readAllBytes();
      validateNotEmpty(content, dialogFileName);

      DialogParser parser = dialogParserProvider.getParser(dialogFileName);
      DialogMap dialogMap = parser.parse(new ByteArrayInputStream(content));
      dialogValidator.validate(dialogMap, dialogFileName);
      return dialogMap;
    } catch (DialogLoadingException e) {
      throw e;
    } catch (Exception e) {
      throw new DialogLoadingException(
          "Failed to load dialog file: '%s'".formatted(dialogFileName), e);
    }
  }

  private void validateNotEmpty(byte[] content, String dialogFileName) {
    String dialogText = new String(content, StandardCharsets.UTF_8);

    if (dialogText.isBlank()) {
      throw new DialogLoadingException("Dialog file is empty: '%s'".formatted(dialogFileName));
    }
  }
}
