package com.mongodb.spring.migration.resource;

import com.mongodb.spring.migration.records.AuthenticationRequest;
import com.mongodb.spring.migration.records.AuthenticationResponse;
import com.mongodb.spring.migration.service.AuthenticationService;
import com.mongodb.spring.migration.service.TokenBlacklistService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthenticationResource {

    private final AuthenticationService authenticationService;

    private final TokenBlacklistService tokenBlacklistService;

    public AuthenticationResource(AuthenticationService authenticationService, TokenBlacklistService tokenBlacklistService) {
        this.authenticationService = authenticationService;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @PostMapping("/token")
    public ResponseEntity<AuthenticationResponse> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) {
        AuthenticationResponse responseMap = authenticationService.authenticate(authenticationRequest.username(), authenticationRequest.password());
        return ResponseEntity.status(HttpStatus.CREATED).body(responseMap);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String jwt = authorizationHeader.substring(7);
            tokenBlacklistService.addTokenToBlacklist(jwt);
        }
        return ResponseEntity.noContent().build();
    }
}