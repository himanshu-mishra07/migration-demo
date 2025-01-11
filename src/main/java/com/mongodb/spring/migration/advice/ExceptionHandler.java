package com.mongodb.spring.migration.advice;


import com.mongodb.spring.migration.exception.BadCredentialsException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@ControllerAdvice
public class ExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ExceptionHandler.class);

    @org.springframework.web.bind.annotation.ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleConstraintViolationException(ConstraintViolationException e) {
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        log.error("Validation completed. violations found: {}", violations);

        Map<String, String> responseObj = new HashMap<>();

        for (ConstraintViolation<?> violation : violations) {
            responseObj.put(violation.getPropertyPath().toString(), violation.getMessage());
        }

        return ResponseEntity.badRequest().body(responseObj);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(ValidationException.class)
    public ResponseEntity<?> handleValidationException(ValidationException e) {
        Map<String, String> responseObj = new HashMap<>();
        responseObj.put("error", e.getMessage());
        log.error("Validation exception: {}", e.getMessage());
        return ResponseEntity.badRequest().body(responseObj);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<?> handleUsernameNotFoundException(UsernameNotFoundException e) {
        log.error("Username not found exception: {}", e.getMessage());
        return ResponseEntity.notFound().build();
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleAuthenticationException(BadCredentialsException e) {
        Map<String, String> responseObj = new HashMap<>();
        responseObj.put("error", e.getMessage());
        log.error("Authentication exception: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseObj);
    }


    @org.springframework.web.bind.annotation.ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGenericException(Exception e) {
        Map<String, String> responseObj = new HashMap<>();
        responseObj.put("error", e.getMessage());
        log.error("Exception occurred: {}", e.getMessage());
        return ResponseEntity.badRequest().body(responseObj);
    }

}
