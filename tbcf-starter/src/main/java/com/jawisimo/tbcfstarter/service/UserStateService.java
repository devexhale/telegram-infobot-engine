package com.jawisimo.tbcfstarter.service;

import com.jawisimo.tbcfstarter.model.UserState;
import com.jawisimo.tbcfstarter.repository.UserStateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserStateService {
    private final UserStateRepository userStateRepository;

    public String getUserStateOrDefault(String chatId, String defaultState) {
        return userStateRepository.findById(chatId)
                .map(UserState::getNodeId)
                .orElse(defaultState);
    }

    public void saveUserState(String chatId, String nextState) {
        UserState userState = new UserState(chatId, nextState);
        userStateRepository.save(userState);
    }

}
