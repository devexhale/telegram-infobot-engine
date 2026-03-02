package com.github.jawisimo.botengine.parser;

import com.github.jawisimo.botengine.exception.DialogLoadingException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Selects an appropriate {@link DialogParser} for a dialog configuration file.
 *
 * <p>Evaluates available parser implementations and returns the single parser that supports the
 * provided file name. Ensures that exactly one matching parser is found.
 *
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DialogParserProvider {

  private final List<DialogParser> parsers;

  /**
   * Returns a parser capable of handling the specified dialog file.
   *
   * @param dialogFileName the dialog configuration file name
   * @return the matching {@link DialogParser}
   * @throws DialogLoadingException if no suitable parser is found or multiple parsers match
   */
  public DialogParser getParser(String dialogFileName) {
    List<DialogParser> matchingParsers =
        parsers.stream().filter(p -> p.canParse(dialogFileName)).toList();

    if (matchingParsers.isEmpty()) {
      throw new DialogLoadingException("No suitable parser found for file: " + dialogFileName);
    }

    if (matchingParsers.size() > 1) {
      throw new DialogLoadingException(
          "Multiple parsers found for file: "
              + dialogFileName
              + " -> "
              + matchingParsers.stream().map(p -> p.getClass().getSimpleName()).toList());
    }

    DialogParser parser = matchingParsers.getFirst();
    log.info("Parsing dialog using {}", parser.getClass().getSimpleName());
    return parser;
  }
}
