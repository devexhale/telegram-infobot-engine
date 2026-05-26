package io.github.devexhale.botengine.validator.properties;

import java.util.List;

public interface PropertiesValidator {

  List<String> findMissingProperties();

  List<String> findInvalidProperties();
}
