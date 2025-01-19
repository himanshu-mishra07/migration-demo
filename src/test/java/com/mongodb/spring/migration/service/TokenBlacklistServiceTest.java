package com.mongodb.spring.migration.service;

import com.mongodb.spring.migration.entity.BlacklistedToken;
import com.mongodb.spring.migration.repo.BlacklistedTokenRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class TokenBlacklistServiceTest {

    @Mock
    private BlacklistedTokenRepo blacklistedTokenRepository;

    @InjectMocks
    private TokenBlacklistService tokenBlacklistService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAddTokenToBlacklist() {
        String token = "dummy_token";
        BlacklistedToken blacklistedToken = new BlacklistedToken(token);

        tokenBlacklistService.addTokenToBlacklist(token);

        ArgumentCaptor<BlacklistedToken> captor = ArgumentCaptor.forClass(BlacklistedToken.class);
        verify(blacklistedTokenRepository, times(1)).save(captor.capture());
        assertEquals(blacklistedToken.getToken(), captor.getValue().getToken());
    }

    @Test
    public void testIsTokenBlacklisted_TokenExists() {
        String token = "dummy_token";
        when(blacklistedTokenRepository.existsByToken(anyString())).thenReturn(true);

        boolean result = tokenBlacklistService.isTokenBlacklisted(token);

        assertTrue(result);
    }

    @Test
    public void testIsTokenBlacklisted_TokenDoesNotExist() {
        String token = "dummy_token";
        when(blacklistedTokenRepository.existsByToken(anyString())).thenReturn(false);

        boolean result = tokenBlacklistService.isTokenBlacklisted(token);

        assertFalse(result);
    }
}