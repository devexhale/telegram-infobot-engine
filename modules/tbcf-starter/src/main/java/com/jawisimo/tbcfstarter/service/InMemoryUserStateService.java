package com.jawisimo.tbcfstarter.service;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ConditionalOnProperty(
        prefix = "telegram.bot",
        name = "enable-last-command",
        havingValue = "false",
        matchIfMissing = true
)
public class InMemoryUserStateService implements UserStateService {
    private final Map<String, String> state = new ConcurrentHashMap<>();

    @Override
    public String getUserStateOrDefault(String chatId, String defaultState) {
        return state.getOrDefault(chatId, defaultState);
    }

    @Override
    public void saveUserState(String chatId, String nextState) {
        state.put(chatId, nextState);
    }

}
