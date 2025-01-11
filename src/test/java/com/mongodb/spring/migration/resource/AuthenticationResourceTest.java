package com.mongodb.spring.migration.resource;

import com.mongodb.spring.migration.records.AuthenticationRequest;
import com.mongodb.spring.migration.service.AuthenticationService;
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

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthenticationResourceTest {

    @Mock
    private AuthenticationService authenticationService;

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
        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("token", "dummy_jwt_token");
        when(authenticationService.authenticate(anyString(), anyString())).thenReturn(responseMap);

        // Act & Assert
        mockMvc.perform(post("/auth/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"testuser\", \"password\":\"testpass\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    public void testCreateAuthenticationTokenService() {
        // Arrange
        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("token", "dummy_jwt_token");
        when(authenticationService.authenticate(anyString(), anyString())).thenReturn(responseMap);

        AuthenticationRequest request = new AuthenticationRequest("testuser", "testpass");

        // Act
        ResponseEntity<Map<String, String>> responseEntity = authenticationResource.createAuthenticationToken(request);

        // Assert
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(responseMap, responseEntity.getBody());
    }
}
