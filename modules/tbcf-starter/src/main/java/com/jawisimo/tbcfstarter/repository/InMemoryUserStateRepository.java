package com.jawisimo.tbcfstarter.repository;

import com.jawisimo.tbcfstarter.dialog.node.model.UserState;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryUserStateRepository {
    private final ConcurrentHashMap<String, UserState> userStates = new ConcurrentHashMap<>();

    public Optional<UserState> findById(String id) {
        return Optional.ofNullable(userStates.get(id));
    }

    public void save(UserState userState) {
        userStates.put(userState.getChatId(), userState);
    }
}
