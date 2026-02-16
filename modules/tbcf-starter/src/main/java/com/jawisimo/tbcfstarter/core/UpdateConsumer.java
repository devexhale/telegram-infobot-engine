package com.jawisimo.tbcfstarter.core;

import com.jawisimo.tbcfstarter.interaction.command.CommandsInitializer;
import com.jawisimo.tbcfstarter.service.UpdateService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@Slf4j
@RequiredArgsConstructor
public class UpdateConsumer implements LongPollingSingleThreadUpdateConsumer {
  private final CommandsInitializer commandsInitializer;
  private final UpdateService updateService;

  @PostConstruct
  public void init() {
    commandsInitializer.setUpCommands();
  }

  @Override
  public void consume(Update update) {
    updateService.onUpdateReceived(update);
  }
}
