package com.jawisimo.tbcfstarter.service;

import com.jawisimo.tbcfstarter.interaction.command.commandset.StartCommand;
import com.jawisimo.tbcfstarter.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageCleanupService {
    private final TelegramClient client;
    private final MessageRepository messageRepository;

    public void deleteMessage(String chatId, Integer messageId) {
        try {
            client.executeAsync(DeleteMessage.builder().chatId(chatId).messageId(messageId).build());
        } catch (TelegramApiException e) {
            log.warn("Failed to delete message: {} from chat: {}", messageId, chatId, e);
        }
    }

    public void deleteRedundantMessage(Message message) {
        if (message == null) return;
        String text = message.getText();
        if (text != null && text.equals(StartCommand.COMMAND_NAME)) return;
        String chatId = message.getChatId().toString();
        Integer messageId = message.getMessageId();
        deleteMessage(chatId, messageId);
    }

    public void clearLastNode(String chatId) {
        List<Integer> messageIds = messageRepository.removeAll(chatId);
        if (messageIds == null || messageIds.isEmpty()) return;

        for (Integer messageId : messageIds) {
            deleteMessage(chatId, messageId);
        }
    }
}
