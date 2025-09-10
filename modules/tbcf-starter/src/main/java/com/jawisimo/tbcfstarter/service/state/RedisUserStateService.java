package com.jawisimo.tbcfstarter.service.state;

import com.jawisimo.tbcfstarter.model.UserState;
import com.jawisimo.tbcfstarter.repository.UserStateRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(
        prefix = "telegram.bot",
        name = "enable-last-command",
        havingValue = "true"
)
public record RedisUserStateService(UserStateRepository userStateRepository)
        implements UserStateService {

    @Override
    public String getUserStateOrDefault(String chatId, String defaultState) {
        return userStateRepository.findById(chatId)
                .map(UserState::getNodeId)
                .orElse(defaultState);
    }

    @Override
    public void saveUserState(String chatId, String nextState) {
        userStateRepository.save(new UserState(chatId, nextState));
    }
}
