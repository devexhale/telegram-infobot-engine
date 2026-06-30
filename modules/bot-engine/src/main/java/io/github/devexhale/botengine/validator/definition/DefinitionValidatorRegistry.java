package io.github.devexhale.botengine.validator.definition;

import io.github.devexhale.botengine.diagnostics.exception.DefinitionInitializationException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

/**
 * Registry for {@link DefinitionValidator} implementations.
 *
 * <p>Maps target domain classes to their corresponding validators and ensures no duplicate
 * validators are registered.
 *
 * @since 1.0
 */
@Component
public class DefinitionValidatorRegistry {

  private final Map<Class<?>, DefinitionValidator<?>> validatorsByType;

  /**
   * Initializes the registry with the provided validators.
   *
   * @param validators the list of validators to register
   * @throws IllegalStateException if a duplicate validator is found
   */
  public DefinitionValidatorRegistry(List<DefinitionValidator<?>> validators) {
    this.validatorsByType =
        validators.stream()
            .collect(
                Collectors.toUnmodifiableMap(
                    DefinitionValidator::targetType,
                    Function.identity(),
                    (left, right) -> {
                      throw new IllegalStateException(
                          "Duplicate definition validators found for type: "
                              + left.targetType().getSimpleName());
                    }));
  }

  /**
   * Retrieves the validator for the specified target type.
   *
   * @param targetType the target domain class
   * @param <T> the target type
   * @return the registered validator
   * @throws DefinitionInitializationException if no validator is found for the type
   */
  @SuppressWarnings("unchecked")
  public <T> DefinitionValidator<T> get(Class<T> targetType) {
    DefinitionValidator<?> validator = validatorsByType.get(targetType);

    if (validator == null) {
      throw new DefinitionInitializationException(
          "No definition validator registered for type: " + targetType.getSimpleName());
    }

    return (DefinitionValidator<T>) validator;
  }
}
