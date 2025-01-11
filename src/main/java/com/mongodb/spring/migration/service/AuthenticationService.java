package com.mongodb.spring.migration.service;

import com.mongodb.spring.migration.exception.BadCredentialsException;
import com.mongodb.spring.migration.util.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtUtil jwtUtil;

    public AuthenticationService(AuthenticationManager authenticationManager,
                                 CustomUserDetailsService customUserDetailsService,
                                 JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.customUserDetailsService = customUserDetailsService;
        this.jwtUtil = jwtUtil;
    }

    public Map<String, String> authenticate(String username, String password) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Incorrect username or password", e);
        }

        final UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
        return Map.of("token", jwtUtil.generateToken(userDetails.getUsername()));
    }
}