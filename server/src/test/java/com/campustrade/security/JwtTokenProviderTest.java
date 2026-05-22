package com.campustrade.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    private JwtTokenProvider provider;

    @BeforeEach
    void setUp() {
        provider = new JwtTokenProvider(
            "test-secret-key-that-is-long-enough-for-hs256",
            3600000L // 1 hour
        );
    }

    @Test
    void shouldGenerateTokenContainingUserId() {
        String token = provider.generateToken(1L, "testuser", "user");
        Claims claims = Jwts.parser()
            .verifyWith(provider.getSigningKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();

        assertEquals(1L, claims.get("userId", Long.class));
        assertEquals("testuser", claims.getSubject());
        assertEquals("user", claims.get("role", String.class));
    }

    @Test
    void shouldValidateCorrectToken() {
        String token = provider.generateToken(1L, "testuser", "user");
        assertTrue(provider.validateToken(token));
    }

    @Test
    void shouldRejectExpiredToken() {
        provider = new JwtTokenProvider("test-secret-key-that-is-long-enough-for-hs256", -1L);
        String token = provider.generateToken(1L, "testuser", "user");
        assertFalse(provider.validateToken(token));
    }

    @Test
    void shouldRejectTamperedToken() {
        String token = provider.generateToken(1L, "testuser", "user");
        String tampered = token.substring(0, token.length() - 4) + "xxxx";
        assertFalse(provider.validateToken(tampered));
    }

    @Test
    void shouldExtractUserIdFromToken() {
        String token = provider.generateToken(42L, "testuser", "user");
        assertEquals(42L, provider.getUserIdFromToken(token));
    }

    @Test
    void shouldExtractUsernameFromToken() {
        String token = provider.generateToken(1L, "testuser", "user");
        assertEquals("testuser", provider.getUsernameFromToken(token));
    }

    @Test
    void shouldExtractRoleFromToken() {
        String token = provider.generateToken(1L, "testuser", "admin");
        assertEquals("admin", provider.getRoleFromToken(token));
    }

    @Test
    void shouldSetCorrectExpiration() {
        String token = provider.generateToken(1L, "testuser", "user");
        Claims claims = Jwts.parser()
            .verifyWith(provider.getSigningKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();

        Date expiration = claims.getExpiration();
        long diff = expiration.getTime() - System.currentTimeMillis();
        assertTrue(diff > 0 && diff <= 3600000L);
    }
}
