package io.github.devexhale.botengine.util;

import lombok.experimental.UtilityClass;

/**
 * Utility class for detecting the current runtime environment.
 *
 * @since 1.0
 */
@UtilityClass
public class EnvironmentDetector {

  private static final String SPRING_TEST_CONTEXT_CLASS =
      "org.springframework.test.context.TestContext";

  /**
   * Determines whether the application is running in a test environment by checking for the
   * presence of {@code spring-test} on the classpath.
   *
   * @return {@code true} if a test environment is detected, {@code false} otherwise
   */
  public static boolean isTestEnvironment() {
    try {
      Class.forName(SPRING_TEST_CONTEXT_CLASS);
      return true;
    } catch (ClassNotFoundException e) {
      return false;
    }
  }
}
