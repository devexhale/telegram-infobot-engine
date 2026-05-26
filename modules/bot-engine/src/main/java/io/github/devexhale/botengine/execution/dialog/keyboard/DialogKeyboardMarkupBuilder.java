package io.github.devexhale.botengine.execution.dialog.keyboard;

import io.github.devexhale.botengine.properties.DialogProperties;
import io.github.devexhale.botengine.domain.dialog.Button;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

/**
 * Builds Telegram keyboard markup from dialog button definitions.
 *
 * <p>Converts dialog {@link Button} models into Telegram reply or inline keyboards. Button layout
 * is controlled by {@link DialogProperties#buttonsPerRow()}.
 *
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
public class DialogKeyboardMarkupBuilder {

  private final DialogProperties dialogProperties;

  /**
   * Builds a reply keyboard layout.
   *
   * @param buttons dialog buttons
   * @return the reply keyboard markup
   */
  ReplyKeyboardMarkup buildReplyKeyboard(List<Button> buttons) {
    List<KeyboardRow> rows =
        splitButtons(
            buttons, btn -> KeyboardButton.builder().text(btn.label()).build(), KeyboardRow::new);

    return ReplyKeyboardMarkup.builder()
        .keyboard(rows)
        .resizeKeyboard(true)
        .oneTimeKeyboard(true)
        .isPersistent(true)
        .build();
  }

  /**
   * Builds an inline keyboard layout.
   *
   * @param buttons dialog buttons
   * @return the inline keyboard markup
   */
  InlineKeyboardMarkup buildInlineKeyboard(List<Button> buttons) {
    List<InlineKeyboardRow> rows =
        splitButtons(
            buttons,
            btn ->
                InlineKeyboardButton.builder()
                    .text(btn.label())
                    .url(btn.url())
                    .callbackData(btn.next())
                    .build(),
            InlineKeyboardRow::new);

    return InlineKeyboardMarkup.builder().keyboard(rows).build();
  }

  /** Splits buttons into rows according to configured buttons-per-row value. */
  private <T, R extends List<T>> List<R> splitButtons(
      List<Button> buttons, Function<Button, T> buttonMapper, Function<List<T>, R> rowSupplier) {

    List<R> rows = new ArrayList<>();
    int buttonsPerRow = dialogProperties.buttonsPerRow();

    for (int i = 0; i < buttons.size(); i += buttonsPerRow) {
      List<T> rowButtons = new ArrayList<>();
      for (int j = 0; j < buttonsPerRow && (i + j) < buttons.size(); j++) {
        rowButtons.add(buttonMapper.apply(buttons.get(i + j)));
      }
      rows.add(rowSupplier.apply(rowButtons));
    }

    return rows;
  }
}
