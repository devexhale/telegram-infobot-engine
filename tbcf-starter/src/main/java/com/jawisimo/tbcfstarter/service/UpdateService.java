package com.jawisimo.tbcfstarter.service;

import com.jawisimo.tbcfstarter.handler.UpdateHandler;
import com.jawisimo.tbcfstarter.model.DialogNode;
import com.jawisimo.tbcfstarter.repository.DialogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UpdateService {
    private final List<UpdateHandler> handlers;
    private final DialogRepository dialogRepository;
    private final TelegramClient client;

    @Async("asyncBotExecutor")
    public void onUpdateReceived(Update update) {
        DialogNode node = null;
        String chatId = update.getMessage().getChatId().toString();

        if (update.hasMessage() && update.getMessage().hasText()) {
            String noteId = update.getMessage().getText();
            node = dialogRepository.getDialogNode(noteId);
        }

        for (UpdateHandler handler : handlers) {
            if (handler.supports(node)) {
                handler.handle(node,chatId);
            }
        }
    }

}
