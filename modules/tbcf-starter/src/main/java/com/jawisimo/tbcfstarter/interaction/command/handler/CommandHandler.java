package com.jawisimo.tbcfstarter.interaction.command.handler;

public interface CommandHandler {

  String getCommandKey();

  void handle(String chatId);
}
