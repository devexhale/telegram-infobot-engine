package com.jawisimo.tbcfstarter.interaction.keyboard;

import com.jawisimo.tbcfstarter.config.BotProperties;
import com.jawisimo.tbcfstarter.interaction.node.model.Button;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class KeyboardMarkupBuilder {
    private final BotProperties botProperties;

    ReplyKeyboardMarkup buildReplyKeyboard(List<Button> buttons) {
        List<KeyboardRow> rows = splitButtons(
                buttons,
                btn -> KeyboardButton.builder().text(btn.getLabel()).build(),
                KeyboardRow::new
        );

        return ReplyKeyboardMarkup.builder()
                .keyboard(rows)
                .resizeKeyboard(true)
                .oneTimeKeyboard(true)
                .isPersistent(true)
                .build();
    }

    InlineKeyboardMarkup buildInlineKeyboard(List<Button> buttons) {
        List<InlineKeyboardRow> rows = splitButtons(
                buttons,
                btn -> InlineKeyboardButton.builder()
                        .text(btn.getLabel())
                        .url(btn.getUrl())
                        .callbackData(btn.getNext())
                        .build(),
                InlineKeyboardRow::new
        );

        return InlineKeyboardMarkup.builder()
                .keyboard(rows)
                .build();
    }

    private <T, R extends List<T>> List<R> splitButtons(
            List<Button> buttons,
            Function<Button, T> buttonMapper,
            Function<List<T>, R> rowSupplier
    ) {
        List<R> rows = new ArrayList<>();
        int buttonsPerRow = botProperties.buttonsPerRow();

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
