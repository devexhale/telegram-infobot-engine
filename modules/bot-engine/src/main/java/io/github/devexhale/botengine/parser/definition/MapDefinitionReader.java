package io.github.devexhale.botengine.parser.definition;

import com.fasterxml.jackson.core.type.TypeReference;

/**
 * Defines the contract for mapping raw parsed data to typed domain objects.
 *
 * @param <T> the target domain type
 * @param <R> the raw parsed type
 * @since 1.0
 */
public interface MapDefinitionReader<T, R> {

  /**
   * Returns the target domain class.
   *
   * @return the target class
   */
  Class<T> targetType();

  /**
   * Returns the type reference for the raw parsed data.
   *
   * @return the raw type reference
   */
  TypeReference<R> rawType();

  /**
   * Maps the raw parsed object to the target domain type.
   *
   * @param raw the raw parsed object
   * @return the mapped domain object
   */
  T map(R raw);
}
