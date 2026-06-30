package io.github.devexhale.botengine.validator.properties;

import java.util.List;

/**
 * Validates application properties to ensure required configurations are present and valid.
 *
 * @since 1.0
 */
public interface PropertiesValidator {

  /**
   * Identifies required properties that are missing from the configuration.
   *
   * @return a list of missing property names or descriptions
   */
  List<String> findMissingProperties();

  /**
   * Identifies properties that have invalid values.
   *
   * @return a list of validation error messages for invalid properties
   */
  List<String> findInvalidProperties();
}
