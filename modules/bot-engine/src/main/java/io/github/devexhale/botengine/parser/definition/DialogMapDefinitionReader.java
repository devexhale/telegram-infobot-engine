package io.github.devexhale.botengine.parser.definition;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.devexhale.botengine.domain.dialog.DialogMap;
import io.github.devexhale.botengine.domain.dialog.DialogNode;

import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class DialogMapDefinitionReader
    implements MapDefinitionReader<DialogMap, Map<String, DialogNode>> {

  private static final TypeReference<Map<String, DialogNode>> RAW_TYPE = new TypeReference<>() {};

  @Override
  public Class<DialogMap> targetType() {
    return DialogMap.class;
  }

  @Override
  public TypeReference<Map<String, DialogNode>> rawType() {
    return RAW_TYPE;
  }

  @Override
  public DialogMap map(Map<String, DialogNode> raw) {
    return new DialogMap(raw);
  }
}
