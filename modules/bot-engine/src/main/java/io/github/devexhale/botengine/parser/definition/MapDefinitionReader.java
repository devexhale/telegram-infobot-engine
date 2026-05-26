package io.github.devexhale.botengine.parser.definition;

import com.fasterxml.jackson.core.type.TypeReference;

public interface MapDefinitionReader<T, R> {

  Class<T> targetType();

  TypeReference<R> rawType();

  T map(R raw);
}
