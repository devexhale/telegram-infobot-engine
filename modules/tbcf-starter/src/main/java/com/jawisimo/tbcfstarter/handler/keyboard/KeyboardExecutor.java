package com.jawisimo.tbcfstarter.handler.keyboard;

import com.jawisimo.tbcfstarter.model.Button;
import com.jawisimo.tbcfstarter.model.ButtonType;
import com.jawisimo.tbcfstarter.model.DialogNode;
import com.jawisimo.tbcfstarter.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class KeyboardExecutor {
    private final TelegramClient client;
    private final KeyboardMarkupBuilder keyboardBuilder;
    private final MessageRepository messageRepository;

    public void execute(DialogNode node, String chatId) {
        List<Button> buttons = node.buttons();

        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .text(node.message())
                .build();

        if (node.buttonType() == ButtonType.REPLY) {
            sendMessage.setReplyMarkup(keyboardBuilder.buildReplyKeyboard(buttons));
        } else {
            sendMessage.setReplyMarkup(keyboardBuilder.buildInlineKeyboard(buttons));
        }

        try {
            Message sent = client.execute(sendMessage);
            messageRepository.save(chatId, sent.getMessageId());
        } catch (TelegramApiException e) {
            log.error("Failed to handle keyboard markup in chat: {}", chatId, e);
        }
    }
}
