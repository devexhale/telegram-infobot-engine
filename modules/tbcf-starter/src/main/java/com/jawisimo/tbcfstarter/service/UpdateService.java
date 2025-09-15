package com.jawisimo.tbcfstarter.service;

import com.jawisimo.tbcfstarter.handler.DialogHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
@Slf4j
@RequiredArgsConstructor
public class UpdateService {
    private final DialogHandler handler;


    @Async("asyncBotVirtualExecutor")
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            handler.handleMessage(update.getMessage());
        } else if (update.hasCallbackQuery()) {
            handler.handleCallback(update.getCallbackQuery());
        } else {
            log.warn("Unsupported update type: {}", update);
        }
    }

}
