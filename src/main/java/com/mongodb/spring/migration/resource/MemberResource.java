package com.mongodb.spring.migration.resource;

import com.mongodb.spring.migration.entity.Member;
import com.mongodb.spring.migration.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rest/members")
public class MemberResource {

    private final MemberService memberService;

    public MemberResource(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping
    public List<Member> listAllMembers() {
        return memberService.listAllMembers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Member> lookupMemberById(@PathVariable String id) {
        return memberService.lookupMemberById(id);
    }

    @PostMapping
    public ResponseEntity<?> createMember(@RequestBody Member member) {
        return ResponseEntity.ok(memberService.registerMember(member));
    }
}
