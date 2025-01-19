package com.mongodb.spring.migration.records;

import com.mongodb.spring.migration.entity.Member;

import java.util.List;

public record MemberResponse(Member member, List<String> roles) {
}
