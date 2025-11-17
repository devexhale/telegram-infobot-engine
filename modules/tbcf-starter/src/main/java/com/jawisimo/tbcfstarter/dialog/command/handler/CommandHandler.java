package com.jawisimo.tbcfstarter.dialog.command.handler;

public interface CommandHandler {

    String getCommandKey();

    void handle(String chatId);
}
