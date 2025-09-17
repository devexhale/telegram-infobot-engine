package com.jawisimo.tbcfstarter.repository;

import com.jawisimo.tbcfstarter.annotation.UserStatePersistent;
import com.jawisimo.tbcfstarter.model.UserState;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
@UserStatePersistent
public interface UserStateRepository extends CrudRepository<UserState, String> {
}
