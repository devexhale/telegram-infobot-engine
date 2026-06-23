package io.github.devexhale.botengine.domain.dialog;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class ButtonTypeTest {

  @Test
  void fromString_shouldReturnCorrectEnum_whenValidValueProvided() {
    assertEquals(ButtonType.INLINE, ButtonType.fromString("INLINE"));
    assertEquals(ButtonType.REPLY, ButtonType.fromString("reply"));
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {" ", "   "})
  void fromString_shouldReturnNull_whenValueIsNullOrEmptyOrBlank(String value) {
    assertNull(ButtonType.fromString(value));
  }

  @Test
  void fromString_shouldReturnUnknown_whenInvalidValueProvided() {
    assertEquals(ButtonType.UNKNOWN, ButtonType.fromString("INVALID_BUTTON"));
    assertEquals(ButtonType.UNKNOWN, ButtonType.fromString("custom_keyboard"));
  }
}
