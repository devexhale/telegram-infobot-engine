package com.jawisimo.tbcfstarter.service;

import com.jawisimo.tbcfstarter.config.BotProperties;
import com.jawisimo.tbcfstarter.model.UserState;
import com.jawisimo.tbcfstarter.repository.UserStateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserStateService {
    private final BotProperties properties;
    private UserStateRepository userStateRepository;

    @Autowired(required = false)
    public void setUserStateRepository(UserStateRepository userStateRepository) {
        if (properties.userStatePersistent()) {
            this.userStateRepository = userStateRepository;
        }
    }

    public String getUserStateOrDefault(String chatId, String defaultState) {
        if (userStatePersistent()) {
            return userStateRepository.findById(chatId)
                    .map(UserState::getNodeId)
                    .orElse(defaultState);
        }

        return defaultState;
    }

    public void saveUserStateIfPersist(String chatId, String nextState) {
        if (userStatePersistent()) {
            userStateRepository.save(new UserState(chatId, nextState));
        }
    }

    private boolean userStatePersistent() {
        return properties.userStatePersistent() && userStateRepository != null;
    }
}
