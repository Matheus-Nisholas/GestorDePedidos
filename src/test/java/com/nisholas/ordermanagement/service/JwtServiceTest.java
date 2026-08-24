package com.nisholas.ordermanagement.service;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.nisholas.ordermanagement.entity.Role;
import com.nisholas.ordermanagement.entity.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private static final String SECRET = "test-secret-key-for-jwt-validation";

    @Test
    void shouldGenerateAndValidateToken() {
        JwtService jwtService = new JwtService(SECRET, 2L);
        User user = User.builder()
                .id(10L)
                .email("user@email.com")
                .password("hash")
                .role(Role.USER)
                .active(true)
                .build();

        String token = jwtService.generateToken(user);

        assertNotNull(token);
        assertEquals("user@email.com", jwtService.getEmailFromToken(token));
        assertEquals(10L, jwtService.getUserIdFromToken(token));
        assertEquals("USER", jwtService.getRoleFromToken(token));
    }

    @Test
    void shouldRejectInvalidToken() {
        JwtService jwtService = new JwtService(SECRET, 2L);

        assertThrows(
                JWTVerificationException.class,
                () -> jwtService.validateToken("token.invalido.aqui")
        );
    }

    @Test
    void shouldRejectExpiredToken() {
        JwtService jwtService = new JwtService(SECRET, -1L);
        User user = User.builder()
                .id(10L)
                .email("user@email.com")
                .password("hash")
                .role(Role.USER)
                .active(true)
                .build();

        String expiredToken = jwtService.generateToken(user);

        assertThrows(
                JWTVerificationException.class,
                () -> jwtService.validateToken(expiredToken)
        );
    }
}
