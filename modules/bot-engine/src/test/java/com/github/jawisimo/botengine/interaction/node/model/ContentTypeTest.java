package com.github.jawisimo.botengine.interaction.node.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ContentTypeTest {

  @Test
  void fromString_shouldReturnText_whenValueIsTEXTUpperCase() {
    String input = "TEXT";
    ContentType expected = ContentType.TEXT;

    ContentType actual = ContentType.fromString(input);

    assertEquals(expected, actual);
  }

  @Test
  void fromString_shouldReturnMedia_whenValueIsMEDIAUpperCase() {
    String input = "MEDIA";
    ContentType expected = ContentType.MEDIA;

    ContentType actual = ContentType.fromString(input);

    assertEquals(expected, actual);
  }

  @Test
  void fromString_shouldReturnText_whenValueIsTextLowerCase() {
    String input = "text";
    ContentType expected = ContentType.TEXT;

    ContentType actual = ContentType.fromString(input);

    assertEquals(expected, actual);
  }

  @Test
  void fromString_shouldReturnMedia_whenValueIsMediaLowerCase() {
    String input = "media";
    ContentType expected = ContentType.MEDIA;

    ContentType actual = ContentType.fromString(input);

    assertEquals(expected, actual);
  }

  @Test
  void fromString_shouldReturnNull_whenValueIsNull() {
    assertNull(ContentType.fromString(null));
  }

  @Test
  void fromString_shouldThrowException_whenValueIsInvalid() {
    String input = "unknown";

    assertThrows(IllegalArgumentException.class, () -> ContentType.fromString(input));
  }
}
