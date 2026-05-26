package io.github.devexhale.botengine.validator.definition;

import io.github.devexhale.botengine.diagnostics.exception.DefinitionInitializationException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

@Component
public class DefinitionValidatorRegistry {

  private final Map<Class<?>, DefinitionValidator<?>> validatorsByType;

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
