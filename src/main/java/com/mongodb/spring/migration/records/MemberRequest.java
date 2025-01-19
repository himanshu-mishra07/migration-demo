package com.mongodb.spring.migration.records;

import com.mongodb.spring.migration.entity.Member;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record MemberRequest(
        @Valid @NotNull Member member,
        List<String> roles,
        String password
) {}
