package com.jawisimo.tbcfstarter.command.handler;

public interface CommandHandler {

    String getCommandKey();

    void handle(String chatId);
}
