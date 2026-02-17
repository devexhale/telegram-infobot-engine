package com.github.jawisimo.tbcfstarter.repository;

import com.github.jawisimo.tbcfstarter.annotation.UserStatePersistent;
import com.github.jawisimo.tbcfstarter.interaction.node.model.UserState;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
@UserStatePersistent
public interface RedisUserStateRepository extends CrudRepository<UserState, String> {}
