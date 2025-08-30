package com.jawisimo.tbcfstarter.service.command;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface CommandService {
    String getCommandKey();
    void executeCommand(String chatId, Update update);
}
