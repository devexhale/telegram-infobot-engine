package com.jawisimo.tbcfstarter.handler.command;

public interface CommandHandler {

    String getCommandKey();

    void handle(String chatId);
}
