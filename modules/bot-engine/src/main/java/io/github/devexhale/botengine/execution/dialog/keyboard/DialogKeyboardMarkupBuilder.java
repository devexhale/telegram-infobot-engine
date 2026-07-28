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
 * Builds Telegram keyboard markups for dialog nodes.
 *
 * <p>Constructs {@link ReplyKeyboardMarkup} and {@link InlineKeyboardMarkup} by splitting buttons
 * into rows based on configured properties.
 *
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
public class DialogKeyboardMarkupBuilder {

  private final DialogProperties dialogProperties;

  /**
   * Builds an {@link InlineKeyboardMarkup} from the provided buttons.
   *
   * @param buttons the list of buttons to include
   * @return the configured inline keyboard markup
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

  /**
   * Builds a {@link ReplyKeyboardMarkup} from the provided buttons.
   *
   * @param buttons the list of buttons to include
   * @return the configured reply keyboard markup
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
   * Splits a list of buttons into rows according to the configured buttons-per-row value.
   *
   * @param buttons the list of buttons to split
   * @param buttonMapper the function to map a {@link Button} to a keyboard button
   * @param rowSupplier the function to create a row from a list of mapped buttons
   * @param <T> the type of the keyboard button
   * @param <R> the type of the row
   * @return the list of rows containing the mapped buttons
   */
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
