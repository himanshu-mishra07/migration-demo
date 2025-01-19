package com.mongodb.spring.migration.resource;

import com.mongodb.spring.migration.records.AuthenticationRequest;
import com.mongodb.spring.migration.records.AuthenticationResponse;
import com.mongodb.spring.migration.service.AuthenticationService;
import com.mongodb.spring.migration.service.TokenBlacklistService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthenticationResourceTest {

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private TokenBlacklistService tokenBlacklistService;

    @InjectMocks
    private AuthenticationResource authenticationResource;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(authenticationResource).build();
    }

    @Test
    public void testCreateAuthenticationToken() throws Exception {
        // Arrange
        AuthenticationResponse authenticationResponse = new AuthenticationResponse("dummy_jwt_token");
        when(authenticationService.authenticate(anyString(), anyString())).thenReturn(authenticationResponse);

        // Act & Assert
        mockMvc.perform(post("/auth/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"testuser\", \"password\":\"testpass\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    public void testCreateAuthenticationTokenService() {
        // Arrange
        AuthenticationResponse authenticationResponse = new AuthenticationResponse("dummy_jwt_token");
        when(authenticationService.authenticate(anyString(), anyString())).thenReturn(authenticationResponse);

        AuthenticationRequest request = new AuthenticationRequest("testuser", "testpass");

        // Act
        ResponseEntity<AuthenticationResponse> responseEntity = authenticationResource.createAuthenticationToken(request);

        // Assert
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(authenticationResponse, responseEntity.getBody());
    }

    @Test
    public void testLogout() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/auth/logout")
                        .header("Authorization", "Bearer dummy_jwt_token"))
                .andExpect(status().isNoContent());

        // Verify
        verify(tokenBlacklistService).addTokenToBlacklist("dummy_jwt_token");
    }
}