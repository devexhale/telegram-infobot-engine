package com.jawisimo.tbcfstarter.service.command;

public interface CommandService {
    String getCommandKey();
    void execute(String chatId);
}
