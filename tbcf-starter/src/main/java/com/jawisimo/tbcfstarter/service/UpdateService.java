package com.jawisimo.tbcfstarter.service;

import com.jawisimo.tbcfstarter.handler.UpdateHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UpdateService {
    private final List<UpdateHandler> handlers;

    @Async("asyncBotExecutor")
    public void onUpdateReceived(Update update) {
        for (UpdateHandler handler : handlers) {
            if (handler.supports(update)) {
                handler.handle(update);
            }
        }
    }

}
