package com.mongodb.spring.migration.resource;

import com.mongodb.spring.migration.entity.Member;
import com.mongodb.spring.migration.records.MemberRequest;
import com.mongodb.spring.migration.records.MemberResponse;
import com.mongodb.spring.migration.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/members")
public class MemberResource {

    private final MemberService memberService;

    public MemberResource(MemberService memberService) {
        this.memberService = memberService;
    }

    @Secured("ROLE_ADMIN")
    @GetMapping
    public List<MemberResponse> listAllMembers() {
        return memberService.listAllMembersWithRoles();
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @GetMapping("/{id}")
    public ResponseEntity<Member> lookupMemberById(@PathVariable String id) {
        return memberService.lookupMemberById(id);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @GetMapping("/email/{email}")
    public ResponseEntity<Member> getMemberByEmail(@PathVariable String email) {
        Member member = memberService.findMemberByEmail(email);
        return ResponseEntity.ok(member);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping
    public ResponseEntity<?> createMember(@Valid @RequestBody MemberRequest memberRequest) {
        Member member = memberService.registerMemberWithRoles(memberRequest.member(), memberRequest.roles(), memberRequest.password());
        return ResponseEntity.status(HttpStatus.CREATED).body(member);
    }

    @PostMapping("/register")
    public ResponseEntity<?> createMemberWithoutRoles(@Valid @RequestBody MemberRequest memberRequest) {
        Member member = memberService.registerMemberWithRoles(memberRequest.member(), null, memberRequest.password());
        return ResponseEntity.status(HttpStatus.CREATED).body(member);
    }

    @Secured("ROLE_ADMIN")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateMember(@PathVariable String id, @Valid @RequestBody MemberRequest memberRequest) {
        Member updatedMember = memberService.updateMember(id, memberRequest.member(), memberRequest.roles(), memberRequest.password());
        return ResponseEntity.ok(updatedMember);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateMemberWithoutRoles(@PathVariable String id, @Valid @RequestBody MemberRequest memberRequest) {
        Member updatedMember = memberService.updateMember(id, memberRequest.member(), null, memberRequest.password());
        return ResponseEntity.ok(updatedMember);
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMember(@PathVariable String id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();
        Member currentUser = memberService.findMemberByEmail(currentUserEmail);

        Map<String, String> responseObj = new HashMap<>();
        responseObj.put("message", "Cannot delete yourself");

        if (currentUser.getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(responseObj);
        }

        memberService.deleteMember(id);
        return ResponseEntity.noContent().build();
    }
}
