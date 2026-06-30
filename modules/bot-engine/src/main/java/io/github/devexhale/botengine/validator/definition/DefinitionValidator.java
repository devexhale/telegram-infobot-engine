package io.github.devexhale.botengine.validator.definition;

/**
 * Strategy interface for validating parsed definition configurations.
 *
 * @param <T> the type of the definition to validate
 * @since 1.0
 */
public interface DefinitionValidator<T> {

  /**
   * Returns the target class this validator supports.
   *
   * @return the target class
   */
  Class<T> targetType();

  /**
   * Validates the parsed definition object.
   *
   * @param target the parsed definition object to validate
   * @param fileName the name of the source file
   */
  void validate(T target, String fileName);
}
