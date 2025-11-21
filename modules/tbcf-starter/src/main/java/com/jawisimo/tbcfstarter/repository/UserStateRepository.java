package com.jawisimo.tbcfstarter.repository;

import com.jawisimo.tbcfstarter.config.BotProperties;
import com.jawisimo.tbcfstarter.interaction.node.model.UserState;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserStateRepository {
    private final BotProperties properties;
    private RedisUserStateRepository redisUserStateRepository;
    private InMemoryUserStateRepository inMemoryUserStateRepository;

    @Autowired(required = false)
    private void setRedisUserStateRepository(RedisUserStateRepository redisUserStateRepository) {
        if (properties.userStatePersistent()) {
            this.redisUserStateRepository = redisUserStateRepository;
        }
    }

    @Autowired(required = false)
    private void setInMemoryUserStateRepository(InMemoryUserStateRepository inMemoryUserStateRepository) {
        if (!properties.userStatePersistent()) {
            this.inMemoryUserStateRepository = inMemoryUserStateRepository;
        }
    }

    public Optional<UserState> findById(String id) {
        if (properties.userStatePersistent()) {
            return redisUserStateRepository.findById(id);
        }

        return inMemoryUserStateRepository.findById(id);
    }

    public void save(UserState userState) {
        if (properties.userStatePersistent()) {
            redisUserStateRepository.save(userState);
        } else {
            inMemoryUserStateRepository.save(userState);
        }
    }
}
