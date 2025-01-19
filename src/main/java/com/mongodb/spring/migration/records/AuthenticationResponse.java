package com.mongodb.spring.migration.records;

import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;

public record AuthenticationResponse(String token) {
}
