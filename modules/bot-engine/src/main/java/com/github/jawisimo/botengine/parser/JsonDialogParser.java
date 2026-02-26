package com.github.jawisimo.botengine.parser;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jawisimo.botengine.exception.DialogLoadingException;
import com.github.jawisimo.botengine.interaction.node.model.DialogMap;
import com.github.jawisimo.botengine.interaction.node.model.DialogNode;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@Component
public class JsonDialogParser implements DialogParser {
  private static final String FORMAT_JSON = ".json";

  private final ObjectMapper jsonMapper = new ObjectMapper();

  @Override
  public boolean canParse(String fileName) {
    return fileName.endsWith(FORMAT_JSON);
  }

  @Override
  public DialogMap parse(InputStream is) {
    try {
      TypeReference<Map<String, DialogNode>> typeRef = new TypeReference<>() {};
      return new DialogMap(jsonMapper.readValue(is, typeRef));
    } catch (IOException e) {
      throw new DialogLoadingException("Failed to parse JSON dialog", e);
    }
  }
}
