package com.github.jawisimo.botengine.interaction.keyboard;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.github.jawisimo.botengine.config.BotProperties;
import com.github.jawisimo.botengine.interaction.node.model.Button;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

@ExtendWith(MockitoExtension.class)
class KeyboardMarkupBuilderTest {

  @Mock private BotProperties botProperties;

  @InjectMocks private KeyboardMarkupBuilder keyboardMarkupBuilder;

  @ParameterizedTest
  @MethodSource("replyKeyboardCases")
  void buildReplyKeyboard_shouldSplitButtonsIntoRowsAndSetFlags_whenButtonsProvided(
      int buttonsPerRow, int buttonsCount, List<Integer> expectedRowSizes) {
    when(botProperties.buttonsPerRow()).thenReturn(buttonsPerRow);

    List<Button> buttons = createButtons(buttonsCount);

    ReplyKeyboardMarkup markup = keyboardMarkupBuilder.buildReplyKeyboard(buttons);

    List<KeyboardRow> rows = markup.getKeyboard();
    List<Integer> actualRowSizes = rows.stream().map(List::size).toList();
    List<String> actualTexts = new ArrayList<>();

    for (KeyboardRow row : rows) {
      for (KeyboardButton button : row) {
        actualTexts.add(button.getText());
      }
    }

    List<String> expectedTexts = buttons.stream().map(Button::label).toList();

    assertEquals(expectedRowSizes, actualRowSizes);
    assertEquals(Boolean.TRUE, markup.getResizeKeyboard());
    assertEquals(Boolean.TRUE, markup.getOneTimeKeyboard());
    assertEquals(Boolean.TRUE, markup.getIsPersistent());
    assertEquals(expectedTexts, actualTexts);
  }

  @ParameterizedTest
  @MethodSource("inlineKeyboardCases")
  void buildInlineKeyboard_shouldSplitButtonsAndMapFields_whenButtonsProvided(
      int buttonsPerRow, int buttonsCount, List<Integer> expectedRowSizes) {
    when(botProperties.buttonsPerRow()).thenReturn(buttonsPerRow);

    List<Button> buttons = createButtons(buttonsCount);

    InlineKeyboardMarkup markup = keyboardMarkupBuilder.buildInlineKeyboard(buttons);

    List<InlineKeyboardRow> rows = markup.getKeyboard();
    List<Integer> actualRowSizes = rows.stream().map(List::size).toList();
    List<InlineKeyboardButton> actualButtons = new ArrayList<>();

    for (InlineKeyboardRow row : rows) {
      actualButtons.addAll(row);
    }

    assertEquals(expectedRowSizes, actualRowSizes);
    assertEquals(buttonsCount, actualButtons.size());

    for (int i = 0; i < actualButtons.size(); i++) {
      InlineKeyboardButton actual = actualButtons.get(i);
      Button expected = buttons.get(i);

      assertEquals(expected.label(), actual.getText());
      assertEquals(expected.url(), actual.getUrl());
      assertEquals(expected.next(), actual.getCallbackData());
    }
  }

  @Test
  void buildReplyKeyboard_shouldReturnEmptyKeyboard_whenButtonsListIsEmpty() {
    int buttonsPerRow = 2;

    when(botProperties.buttonsPerRow()).thenReturn(buttonsPerRow);

    List<Button> buttons = List.of();

    ReplyKeyboardMarkup markup = keyboardMarkupBuilder.buildReplyKeyboard(buttons);

    assertNotNull(markup.getKeyboard());
    assertTrue(markup.getKeyboard().isEmpty());
  }

  @Test
  void buildInlineKeyboard_shouldReturnEmptyKeyboard_whenButtonsListIsEmpty() {
    int buttonsPerRow = 2;

    when(botProperties.buttonsPerRow()).thenReturn(buttonsPerRow);

    List<Button> buttons = List.of();

    InlineKeyboardMarkup markup = keyboardMarkupBuilder.buildInlineKeyboard(buttons);

    assertNotNull(markup.getKeyboard());
    assertTrue(markup.getKeyboard().isEmpty());
  }

  private static List<Button> createButtons(int count) {
    List<Button> buttons = new ArrayList<>();

    for (int i = 1; i <= count; i++) {
      String label = "label-" + i;
      String next = "next-" + i;
      String url = "https://example.com/" + i;

      buttons.add(new Button(label, next, url));
    }

    return buttons;
  }

  static Stream<Arguments> replyKeyboardCases() {
    return Stream.of(
        Arguments.of(2, 5, List.of(2, 2, 1)),
        Arguments.of(3, 3, List.of(3)),
        Arguments.of(4, 1, List.of(1)),
        Arguments.of(1, 4, List.of(1, 1, 1, 1)),
        Arguments.of(2, 0, List.of()));
  }

  static Stream<Arguments> inlineKeyboardCases() {
    return Stream.of(
        Arguments.of(2, 5, List.of(2, 2, 1)),
        Arguments.of(3, 4, List.of(3, 1)),
        Arguments.of(1, 3, List.of(1, 1, 1)),
        Arguments.of(2, 0, List.of()));
  }
}
