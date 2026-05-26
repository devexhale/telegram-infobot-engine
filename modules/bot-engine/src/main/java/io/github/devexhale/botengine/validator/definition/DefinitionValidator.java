package io.github.devexhale.botengine.validator.definition;

public interface DefinitionValidator<T> {

  Class<T> targetType();

  void validate(T target, String fileName);
}
