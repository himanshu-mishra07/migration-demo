package com.mongodb.spring.migration.service;


import com.mongodb.spring.migration.entity.Member;
import com.mongodb.spring.migration.repo.MemberRepo;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MemberService {

    private static final Logger log = LoggerFactory.getLogger(MemberService.class);

    private final MemberRepo memberRepo;

    private final Validator validator;

    public MemberService(MemberRepo memberRepo, Validator validator) {
        this.memberRepo = memberRepo;
        this.validator = validator;
    }

    public ResponseEntity<Member> lookupMemberById(String id) {
        return memberRepo.findById(id).map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    public List<Member> listAllMembers() {
        return memberRepo.findAll(Sort.by(Sort.Direction.ASC, "name"));
    }

    public ResponseEntity<?> register(Member member) {
        try {
            validateMember(member);
            log.info("Register member: {}", member.getName());
            return ResponseEntity.ok().body(memberRepo.save(member));
        }  catch (ConstraintViolationException ce) {
            return createViolationResponse(ce.getConstraintViolations());
        } catch (ValidationException e) {
            // Handle the unique constrain violation
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("email", "Email taken");
            return ResponseEntity.badRequest().body(responseObj);
        } catch (Exception e) {
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(responseObj);
        }
    }


    public void registerMember(Member member) {
        validateMember(member);
        log.info("Register member via UI: {}", member.getName());
        memberRepo.save(member);
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
    private void validateMember(Member member) throws ValidationException {
        // Create a bean validator and check for issues.
        Set<ConstraintViolation<Member>> violations = validator.validate(member);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(new HashSet<>(violations));
        }

        // Check the uniqueness of the email address
        if (emailAlreadyExists(member.getEmail())) {
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

    private ResponseEntity<?> createViolationResponse(Set<ConstraintViolation<?>> violations) {
        log.info("Validation completed. violations found: {}", violations.size());

        Map<String, String> responseObj = new HashMap<>();

        for (ConstraintViolation<?> violation : violations) {
            responseObj.put(violation.getPropertyPath().toString(), violation.getMessage());
        }

        return ResponseEntity.badRequest().body(responseObj);
    }
}
