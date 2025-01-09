package com.mongodb.spring.migration.service;

import com.mongodb.spring.migration.entity.Member;
import com.mongodb.spring.migration.repo.MemberRepo;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class MemberServiceTest {

    @Mock
    private MemberRepo memberRepo;

    @Mock
    private Validator validator;

    @InjectMocks
    private MemberService memberService;

    private Member member;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        member = new Member();
        member.setId("1");
        member.setName("John Doe");
        member.setEmail("john.doe@example.com");
        member.setPhoneNumber("1234567890");
    }

    @Test
    public void testLookupMemberById() {
        when(memberRepo.findById("1")).thenReturn(Optional.of(member));
        ResponseEntity<Member> response = memberService.lookupMemberById("1");
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals(member, response.getBody());
    }

    @Test
    public void testLookupMemberById_NotFound() {
        when(memberRepo.findById("1")).thenReturn(Optional.empty());
        ResponseEntity<Member> response = memberService.lookupMemberById("1");
        assertTrue(response.getStatusCode().is4xxClientError());
    }

    @Test
    public void testListAllMembers() {
        List<Member> members = Arrays.asList(member);
        when(memberRepo.findAll(Sort.by(Sort.Direction.ASC, "name"))).thenReturn(members);
        List<Member> result = memberService.listAllMembers();
        assertEquals(1, result.size());
        assertEquals(member, result.get(0));
    }

    @Test
    public void testRegisterMember() {
        when(validator.validate(any(Member.class))).thenReturn(Collections.emptySet());
        when(memberRepo.save(any(Member.class))).thenReturn(member);
        Member result = memberService.registerMember(member);
        assertEquals(member, result);
    }

    @Test
    public void testRegisterMember_ValidationException() {
        when(validator.validate(any(Member.class))).thenReturn(Collections.singleton(mock(ConstraintViolation.class)));
        assertThrows(ConstraintViolationException.class, () -> memberService.registerMember(member));
    }

    @Test
    public void testRegisterMember_EmailAlreadyExists() {
        when(validator.validate(any(Member.class))).thenReturn(Collections.emptySet());
        when(memberRepo.findByEmail(anyString())).thenReturn(Optional.of(member));
        assertThrows(ValidationException.class, () -> memberService.registerMember(member));
    }
}
