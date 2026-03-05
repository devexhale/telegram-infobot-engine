package com.github.jawisimo.botengine.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class EnvironmentDetectorTest {

  @Test
  void isTestEnvironment_shouldReturnTrue_whenSpringTestIsInClasspath() {
    assertTrue(EnvironmentDetector.isTestEnvironment());
  }
}
