package com.mongodb.spring.migration.service;


import com.mongodb.spring.migration.entity.AuthUser;
import com.mongodb.spring.migration.entity.Member;
import com.mongodb.spring.migration.records.MemberResponse;
import com.mongodb.spring.migration.repo.MemberRepo;
import com.mongodb.spring.migration.repo.UserRepo;
import jakarta.validation.*;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MemberService {

    private static final Logger log = LoggerFactory.getLogger(MemberService.class);

    private final MemberRepo memberRepo;

    private final Validator validator;

    private final PasswordEncoder passwordEncoder;

    private final UserRepo authUserRepo;;

    public MemberService(MemberRepo memberRepo, Validator validator, PasswordEncoder passwordEncoder, UserRepo authUserRepo) {
        this.memberRepo = memberRepo;
        this.validator = validator;
        this.passwordEncoder = passwordEncoder;
        this.authUserRepo = authUserRepo;
    }

    public ResponseEntity<Member> lookupMemberById(String id) {
        return memberRepo.findById(id).map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    public Member findMemberByEmail(String email) {
        return memberRepo.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("Member not found"));
    }

    public List<MemberResponse> listAllMembersWithRoles() {
        List<Member> members = listAllMembers();
        return members.stream()
                .map(member -> {
                    List<String> roles = findRolesByEmail(member.getEmail());
                    return new MemberResponse(member, roles);
                })
                .collect(Collectors.toList());
    }

    public List<String> findRolesByEmail(String email) {
        AuthUser authUser = authUserRepo.findByUsername(email).orElseThrow(() -> new IllegalArgumentException("AuthUser not found"));
        return authUser.getRoles();
    }

    public List<Member> listAllMembers() {
        return memberRepo.findAll(Sort.by(Sort.Direction.ASC, "name"));
    }

    public Member registerMember(Member member) {
        validateMember(member, false);
        log.info("Register member via UI: {}", member.getName());
        return memberRepo.save(member);
    }

    @Transactional
    public Member registerMemberWithRoles(Member member, List<String> roles, String password) {
        validateMember(member, false);
        if (password == null || password.isEmpty()) {
            password = generateRandomPassword();
        }
        log.info("Register member via API: {}, pass: {}", member.getName(), password);
        String encodedPassword = passwordEncoder.encode(password);
        Member insertedMember = memberRepo.save(member);

        if (roles == null || roles.isEmpty()) {
            roles = Collections.singletonList("ROLE_USER");
        } else {
            roles = roles.stream()
                    .map(role -> "ROLE_" + role)
                    .collect(Collectors.toList());
            validateRoles(roles);
        }

        AuthUser authUser = new AuthUser();
        authUser.setUsername(member.getEmail());
        authUser.setPassword(encodedPassword);
        authUser.setRoles(roles);
        authUserRepo.save(authUser);
        return insertedMember;
    }

    @Transactional
    public Member updateMember(String id, Member member, List<String> roles, String password) {
        validateMember(member, true);
        Member existingMember = memberRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Member not found"));
        existingMember.setName(member.getName());
        existingMember.setPhoneNumber(member.getPhoneNumber());
        memberRepo.save(existingMember);

        AuthUser authUser = authUserRepo.findByUsername(existingMember.getEmail()).orElseThrow(() -> new IllegalArgumentException("AuthUser not found"));
        if (password != null && !password.isEmpty()) {
            authUser.setPassword(passwordEncoder.encode(password));
        }
        if (roles != null && !roles.isEmpty()) {
            roles = roles.stream().map(role -> "ROLE_" + role).collect(Collectors.toList());
            validateRoles(roles);
            authUser.setRoles(roles);
        }
        authUserRepo.save(authUser);

        return existingMember;
    }

    @Transactional
    public void deleteMember(String id) {
        Member member = memberRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Member not found"));
        memberRepo.delete(member);

        AuthUser authUser = authUserRepo.findByUsername(member.getEmail()).orElseThrow(() -> new IllegalArgumentException("AuthUser not found"));
        authUserRepo.delete(authUser);
    }

    private void validateRoles(List<String> roles) {
        Set<String> uniqueRoles = new HashSet<>();
        for (String role : roles) {
            if (!role.equals("ROLE_ADMIN") && !role.equals("ROLE_USER")) {
                throw new IllegalArgumentException("Invalid role: " + role);
            }
            if (!uniqueRoles.add(role)) {
                throw new IllegalArgumentException("Duplicate role: " + role);
            }
        }
    }

    private String generateRandomPassword() {
        return UUID.randomUUID().toString().substring(0, 8);
    }


    /**
     * <p>
     * Validates the given Member variable and throws validation exceptions based on the type of error. If the error is standard
     * bean validation errors then it will throw a ConstraintValidationException with the set of the constraints violated.
     * </p>
     * <p>
     * If the error is caused because an existing member with the same email is registered it throws a regular validation
     * exception so that it can be interpreted separately.
     * </p>
     *
     * @param member Member to be validated
     * @throws ConstraintViolationException If Bean Validation errors exist
     * @throws ValidationException If member with the same email already exists
     */
    private void validateMember(Member member, boolean isUpdate) throws ValidationException {
        // Create a bean validator and check for issues.
        Set<ConstraintViolation<Member>> violations = validator.validate(member);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(new HashSet<>(violations));
        }

        // Check the uniqueness of the email address only if it's not an update operation
        if (!isUpdate && emailAlreadyExists(member.getEmail())) {
            throw new ValidationException("Unique Email Violation");
        }
    }

    /**
     * Checks if a member with the same email address is already registered. This is the only way to easily capture the
     * "@UniqueConstraint(columnNames = "email")" constraint from the Member class.
     *
     * @param email The email to check
     * @return True if the email already exists, and false otherwise
     */
    public boolean emailAlreadyExists(String email) {
        return memberRepo.findByEmail(email).isPresent();
    }

}
