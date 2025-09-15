package com.jawisimo.tbcfstarter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
@Slf4j
@RequiredArgsConstructor
public class UpdateService {
    private final InputService inputService;


    @Async("asyncBotVirtualExecutor")
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            inputService.executeMessage(update.getMessage());
        } else if (update.hasCallbackQuery()) {
            inputService.executeCallback(update.getCallbackQuery());
        } else {
            log.warn("Unsupported update type: {}", update);
        }
    }

}
