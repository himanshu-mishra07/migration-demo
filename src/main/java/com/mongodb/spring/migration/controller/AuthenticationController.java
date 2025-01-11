package com.mongodb.spring.migration.controller;

import com.mongodb.spring.migration.records.AuthenticationRequest;
import com.mongodb.spring.migration.service.AuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/token")
    public ResponseEntity<Map<String, String>> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) {
        Map<String, String> responseMap = authenticationService.authenticate(authenticationRequest.username(), authenticationRequest.password());
        return ResponseEntity.status(HttpStatus.CREATED).body(responseMap);
    }
}