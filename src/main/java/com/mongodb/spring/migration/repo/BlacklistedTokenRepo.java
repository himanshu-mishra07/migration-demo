package com.mongodb.spring.migration.repo;

import com.mongodb.spring.migration.entity.BlacklistedToken;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BlacklistedTokenRepo extends MongoRepository<BlacklistedToken, String> {
    boolean existsByToken(String token);
}
