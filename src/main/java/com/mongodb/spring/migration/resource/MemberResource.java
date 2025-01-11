package com.mongodb.spring.migration.resource;

import com.mongodb.spring.migration.entity.Member;
import com.mongodb.spring.migration.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @Secured("ROLE_ADMIN")
    @PostMapping
    public ResponseEntity<?> createMember(@Valid @RequestBody Member member) {
        return ResponseEntity.status(HttpStatus.CREATED).body(memberService.registerMember(member));
    }
}
