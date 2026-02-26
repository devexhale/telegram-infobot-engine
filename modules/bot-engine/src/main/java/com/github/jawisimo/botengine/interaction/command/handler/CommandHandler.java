package com.github.jawisimo.botengine.interaction.command.handler;

public interface CommandHandler {

  String getCommandKey();

  void handle(String chatId);
}
