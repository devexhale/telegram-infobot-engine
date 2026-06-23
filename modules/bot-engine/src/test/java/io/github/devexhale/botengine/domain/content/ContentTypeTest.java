package io.github.devexhale.botengine.domain.content;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class ContentTypeTest {

  @Test
  void fromString_shouldReturnCorrectEnum_whenValidValueProvided() {
    assertEquals(ContentType.TEXT, ContentType.fromString("TEXT"));
    assertEquals(ContentType.PHOTO, ContentType.fromString("photo"));
    assertEquals(ContentType.AUDIO, ContentType.fromString("AuDiO"));
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {" ", "   "})
  void fromString_shouldReturnNull_whenValueIsNullOrEmptyOrBlank(String value) {
    assertNull(ContentType.fromString(value));
  }

  @Test
  void fromString_shouldReturnUnknown_whenInvalidValueProvided() {
    assertEquals(ContentType.UNKNOWN, ContentType.fromString("INVALID_TYPE"));
    assertEquals(ContentType.UNKNOWN, ContentType.fromString("random_text"));
  }
}
