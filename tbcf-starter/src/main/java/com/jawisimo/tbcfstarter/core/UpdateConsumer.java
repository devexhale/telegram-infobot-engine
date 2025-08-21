package com.jawisimo.tbcfstarter.core;

import com.jawisimo.tbcfstarter.command.CommandsInitializer;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class UpdateConsumer implements LongPollingSingleThreadUpdateConsumer {
    private final CommandsInitializer commandsInitializer;

    @PostConstruct
    public void init() {
        commandsInitializer.setUpCommands();
    }

    @Override
    public void consume(Update update) {

    }
}
