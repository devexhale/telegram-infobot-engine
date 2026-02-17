package com.github.jawisimo.tbcfstarter.parser;

import com.github.jawisimo.tbcfstarter.exception.DialogLoadingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DialogParserProvider {
  private final List<DialogParser> parsers;

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
    return matchingParsers.getFirst();
  }
}
