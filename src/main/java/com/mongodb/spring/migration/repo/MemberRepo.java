package com.mongodb.spring.migration.repo;

import com.mongodb.spring.migration.entity.Member;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface MemberRepo extends MongoRepository<Member, String> {

    Optional<Member> findByEmail(String email);

}
