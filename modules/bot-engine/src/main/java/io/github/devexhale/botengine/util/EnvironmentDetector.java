package io.github.devexhale.botengine.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Utility class for detecting the current runtime environment.
 *
 * @since 1.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EnvironmentDetector {

  /**
   * Determines whether the application is running in a test environment by checking for the
   * presence of {@code spring-test} on the classpath.
   *
   * @return {@code true} if a test environment is detected, {@code false} otherwise
   */
  public static boolean isTestEnvironment() {
    try {
      Class.forName("org.springframework.test.context.TestContext");
      return true;
    } catch (ClassNotFoundException e) {
      return false;
    }
  }
}
