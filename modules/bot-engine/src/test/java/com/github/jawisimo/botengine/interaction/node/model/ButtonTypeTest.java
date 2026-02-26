package com.github.jawisimo.botengine.interaction.node.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ButtonTypeTest {

  @Test
  void fromString_shouldReturnInline_whenValueIsInlineUpperCase() {
    String input = "INLINE";

    ButtonType expected = ButtonType.INLINE;
    ButtonType actual = ButtonType.fromString(input);

    assertEquals(expected, actual);
  }

  @Test
  void fromString_shouldReturnReply_whenValueIsReplyUpperCase() {
    String input = "REPLY";

    ButtonType expected = ButtonType.REPLY;
    ButtonType actual = ButtonType.fromString(input);

    assertEquals(expected, actual);
  }

  @Test
  void fromString_shouldReturnInline_whenValueIsInlineLowerCase() {
    String input = "inline";

    ButtonType expected = ButtonType.INLINE;
    ButtonType actual = ButtonType.fromString(input);

    assertEquals(expected, actual);
  }

  @Test
  void fromString_shouldReturnReply_whenValueIsReplyLowerCase() {
    String input = "reply";

    ButtonType expected = ButtonType.REPLY;
    ButtonType actual = ButtonType.fromString(input);

    assertEquals(expected, actual);
  }

  @Test
  void fromString_shouldReturnNull_whenValueIsNull() {
    assertNull(ButtonType.fromString(null));
  }

  @Test
  void fromString_shouldThrowException_whenValueIsInvalid() {
    String input = "unknown";

    assertThrows(IllegalArgumentException.class, () -> ButtonType.fromString(input));
  }
}
