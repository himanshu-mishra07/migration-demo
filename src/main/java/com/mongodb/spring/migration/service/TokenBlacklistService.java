package com.mongodb.spring.migration.service;

import com.mongodb.spring.migration.entity.BlacklistedToken;
import com.mongodb.spring.migration.repo.BlacklistedTokenRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TokenBlacklistService {

    private final BlacklistedTokenRepo blacklistedTokenRepository;

    public TokenBlacklistService(BlacklistedTokenRepo blacklistedTokenRepository) {
        this.blacklistedTokenRepository = blacklistedTokenRepository;
    }

    public void addTokenToBlacklist(String token) {
        BlacklistedToken blacklistedToken = new BlacklistedToken(token);
        blacklistedTokenRepository.save(blacklistedToken);
    }

    public boolean isTokenBlacklisted(String token) {
        return blacklistedTokenRepository.existsByToken(token);
    }
}