package com.mongodb.spring.migration.repo;

import com.mongodb.spring.migration.entity.AuthUser;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepo extends MongoRepository<AuthUser, String> {
    Optional<AuthUser> findByUsername(String username);
}
