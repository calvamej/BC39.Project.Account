package com.bootcamp.project.account.repository;

import com.bootcamp.project.account.entity.AccountEntity;
import org.bson.types.ObjectId;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface AccountRepository extends ReactiveCrudRepository<AccountEntity, ObjectId> {
}
