package top.cloudlab.auth.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;

import org.junit.jupiter.api.Test;

import top.cloudlab.auth.domain.access.TokenClaims;

class TokenClaimsTest {

    @Test
    void testBuilderSuccess() {
        TokenClaims claims = TokenClaims.of(
            "test-id",
            "user-123",
            "auth-service",
            LocalDateTime.now(),
            3600L,
            Map.of("scope", "read")
        );

        assertNotNull(claims);
        assertEquals("test-id", claims.getId());
        assertEquals("user-123", claims.getSubject());
        assertEquals("auth-service", claims.getIssuer());
    }

    @Test
    void testGetIssuedAt() {
        LocalDateTime now = LocalDateTime.now();
        TokenClaims claims = TokenClaims.of(null, null, null, now, 3600L, null);

        Instant issuedAt = claims.getIssuedAt();
        assertNotNull(issuedAt);
    }

    @Test
    void testGetIssuedAtWithNull() {
        TokenClaims claims = TokenClaims.of(null, null, null, null, null, null);

        assertEquals(null, claims.getIssuedAt());
    }

    @Test
    void testGetExpiresAt() {
        LocalDateTime now = LocalDateTime.now();
        TokenClaims claims = TokenClaims.of(null, null, null, now, 3600L, null);

        Instant expiresAt = claims.getExpiresAt();
        assertNotNull(expiresAt);
        assertEquals(3600L, expiresAt.getEpochSecond() - claims.getIssuedAt().getEpochSecond());
    }

    @Test
    void testGetExpiresAtWithNull() {
        TokenClaims claims = TokenClaims.of(null, null, null, LocalDateTime.now(), null, null);

        assertEquals(null, claims.getExpiresAt());
    }

    @Test
    void testGetExpiresAtWithNullCreateTime() {
        TokenClaims claims = TokenClaims.of(null, null, null, null, 3600L, null);

        assertEquals(null, claims.getExpiresAt());
    }

    @Test
    void testPayload() {
        Map<String, Object> payload = Map.of(
                "scope", "read,write",
                "tenantId", "tenant-123"
        );
        TokenClaims claims = TokenClaims.of(null, null, null, null, null, payload);

        assertEquals(payload, claims.getPayload());
    }
}
