package com.jawisimo.tbcfstarter.handler;

import com.jawisimo.tbcfstarter.config.BotProperties;
import com.jawisimo.tbcfstarter.model.Button;
import com.jawisimo.tbcfstarter.model.ButtonType;
import com.jawisimo.tbcfstarter.model.DialogNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Component
@Slf4j
@RequiredArgsConstructor
public class KeyboardMarkupHandler {
    private final BotProperties botProperties;
    private final TelegramClient client;

    /**
     * Виконує клавіатуру: будує і одразу відправляє в чат.
     *
     * @param node   нода з кнопками та типом
     * @param chatId ідентифікатор чату
     */
    public Message handle(DialogNode node, String chatId) {
        List<Button> buttons = node.buttons();

        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .text(node.question())
                .build();

        if (node.buttonType() == ButtonType.REPLY) {
            sendMessage.setReplyMarkup(createReplyKeyboard(buttons));
        } else {
            sendMessage.setReplyMarkup(createInlineKeyboard(buttons));
        }

        try {
            return client.execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Telegram API Exception: {}", e.getMessage(), e);
            return null;
        }
    }

    private InlineKeyboardMarkup createInlineKeyboard(List<Button> buttons) {
        List<InlineKeyboardRow> rows = splitButtons(
                buttons,
                btn -> InlineKeyboardButton.builder()
                        .text(btn.getLabel())
                        .callbackData(btn.getNext())
                        .build(),
                InlineKeyboardRow::new
        );

        return InlineKeyboardMarkup.builder()
                .keyboard(rows)
                .build();
    }

    private ReplyKeyboardMarkup createReplyKeyboard(List<Button> buttons) {
        List<KeyboardRow> rows = splitButtons(
                buttons,
                btn -> KeyboardButton.builder()
                        .text(btn.getLabel())
                        .build(),
                KeyboardRow::new
        );

        return ReplyKeyboardMarkup.builder()
                .keyboard(rows)
                .resizeKeyboard(true)
                .oneTimeKeyboard(false)
                .isPersistent(true)
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

