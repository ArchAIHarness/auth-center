package top.cloudlab.auth.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;

import top.cloudlab.auth.domain.access.AccessToken;
import top.cloudlab.auth.domain.access.AccessTokenId;
import top.cloudlab.auth.domain.access.AuthType;
import top.cloudlab.auth.domain.access.TokenClaims;
import top.cloudlab.auth.domain.access.TokenStatus;
import top.cloudlab.auth.domain.access.TokenType;
import top.cloudlab.auth.domain.access.Tokener;

class AccessTokenTest {

    public static class TokenClaimsGenerator implements Tokener {

        @Override
        public String generate(TokenClaims claims, String secret) {
            return JWT.create()
                    .withJWTId(claims.getId())
                    .withSubject(claims.getSubject())
                    .withIssuer(claims.getIssuer())
                    .withIssuedAt(claims.getIssuedAt() == null ? new Date().toInstant() : claims.getIssuedAt())
                    .withExpiresAt(claims.getExpiresAt() == null ? null : claims.getExpiresAt())
                    .withPayload(claims.getPayload())
                    .sign(Algorithm.HMAC512(secret));
        }

        @Override
        public TokenClaims parse(String token) {
            var decoded = JWT.decode(token);
            Map<String, Object> claims = new HashMap<>();
            decoded.getClaims().forEach((key, value) -> claims.put(key, value.as(Object.class)));
            return TokenClaims.of(
                decoded.getId(),
                decoded.getSubject(),
                decoded.getIssuer(),
                decoded.getIssuedAt() == null ? null
                        : LocalDateTime.ofInstant(decoded.getIssuedAt().toInstant(), ZoneId.systemDefault()),
                decoded.getExpiresAt() == null || decoded.getIssuedAt() == null ? null
                        : decoded.getExpiresAt().toInstant().getEpochSecond()
                                - decoded.getIssuedAt().toInstant().getEpochSecond(),
                claims
            );
        }

        @Override
        public TokenClaims validate(String token, String secret) {
            TokenClaims claims = parse(token);
            Algorithm algorithm = Algorithm.HMAC512(secret);
            JWTVerifier verifier = JWT.require(algorithm).build();
            verifier.verify(token);
            return claims;
        }

    }

    @Test
    void testCreateSuccess() {
        AccessToken token = AccessToken.reconstruct(
                AccessTokenId.of("123"),
                new TokenClaimsGenerator(),
                "456",
                "789",
                TokenType.Bearer,
                AuthType.Auth,
                null,
                "secret",
                LocalDateTime.now(),
                3600L,
                7200L,
                TokenStatus.VALID
        );
        assertNotNull(token);
        assertEquals(AccessTokenId.of("123"), token.getId());
        assertEquals("456", token.getUserId());
        assertEquals("789", token.getIssuerId());
        assertEquals(TokenType.Bearer, token.getTokenType());
        assertEquals(AuthType.Auth, token.getAuthType());
    }

    @Test
    void testGetAccessToken() {
        AccessToken token = AccessToken.reconstruct(
                AccessTokenId.of("123"),
                new TokenClaimsGenerator(),
                "456",
                "789",
                TokenType.Bearer,
                AuthType.Auth,
                java.util.Set.of("scope1", "scope2"),
                "secret",
                LocalDateTime.now(),
                3600L,
                7200L,
                TokenStatus.VALID
        );
        String accessToken = token.getAccessToken();
        assertNotNull(accessToken, "access token should not be null");
    }

    @Test
    void testGetRefreshToken() {
        AccessToken token = AccessToken.reconstruct(
                AccessTokenId.of("123"),
                new TokenClaimsGenerator(),
                "456",
                "789",
                TokenType.Bearer,
                AuthType.Auth,
                java.util.Set.of(),
                "secret",
                LocalDateTime.now(),
                3600L,
                7200L,
                TokenStatus.VALID
        );
        String refreshToken = token.getRefreshToken();
        assertNotNull(refreshToken, "refresh token should not be null");
    }

    @Test
    void testAccessTokenAndRefreshTokenDifferent() {
        AccessToken token = AccessToken.reconstruct(
                AccessTokenId.of("123"),
                new TokenClaimsGenerator(),
                "456",
                "789",
                TokenType.Bearer,
                AuthType.Auth,
                java.util.Set.of(),
                "secret",
                LocalDateTime.now(),
                3600L,
                7200L,
                TokenStatus.VALID
        );
        String accessToken = token.getAccessToken();
        String refreshToken = token.getRefreshToken();
        assertNotEquals(accessToken, refreshToken, "access token and refresh token should be different");
    }

    @Test
    void testRefreshToken() {
        AccessToken token = AccessToken.reconstruct(
                AccessTokenId.of("123"),
                new TokenClaimsGenerator(),
                "456",
                "789",
                TokenType.Bearer,
                AuthType.Auth,
                java.util.Set.of(),
                "secret",
                LocalDateTime.now(),
                3600L,
                7200L,
                TokenStatus.VALID
        );

        String oldAccessToken = token.getAccessToken();
        String oldRefreshToken = token.getRefreshToken();

        AccessToken newAccessToken = token.refresh();

        assertNotNull(newAccessToken, "new access token should not be null");
        assertNotEquals(oldAccessToken, newAccessToken.getAccessToken(),
                "new access token should be different from old one");
        assertNotNull(newAccessToken.getRefreshToken(), "new refresh token should not be null");
        assertNotEquals(oldRefreshToken, newAccessToken.getRefreshToken(),
                "new refresh token should be different from old one");
    }

    @Test
    void testIsAuthToken() {
        AccessToken authToken = AccessToken.create(new TokenClaimsGenerator(), "456", "789", AuthType.Auth, java.util.Set.of(), "secret");
        assertTrue(authToken.isAuthToken());
        assertFalse(authToken.isOAuthToken());
    }

    @Test
    void testIsOAuthToken() {
        AccessToken oauthToken = AccessToken.create(new TokenClaimsGenerator(), "456", "789", AuthType.OAuth, java.util.Set.of(), "secret");
        assertTrue(oauthToken.isOAuthToken());
        assertFalse(oauthToken.isAuthToken());
    }

    @Test
    void testRevokeToken() {
        AccessToken token = AccessToken.reconstruct(
                AccessTokenId.of("123"),
                new TokenClaimsGenerator(),
                "456",
                "789",
                TokenType.Bearer,
                AuthType.Auth,
                java.util.Set.of(),
                "secret",
                LocalDateTime.now(),
                3600L,
                7200L,
                TokenStatus.VALID
        );

        assertEquals(TokenStatus.VALID, token.getStatus());

        token.revoke();

        assertEquals(TokenStatus.REVOKED, token.getStatus());
    }

    @Test
    void testRefreshExpiredToken() {
        AccessToken token = AccessToken.reconstruct(
                AccessTokenId.of("123"),
                new TokenClaimsGenerator(),
                "456",
                "789",
                TokenType.Bearer,
                AuthType.Auth,
                null,
                "secret",
                LocalDateTime.now(),
                3600L,
                7200L,
                TokenStatus.REVOKED
        );
        
        assertThrows(IllegalStateException.class, () -> token.refresh());
    }

    @Test
    void testGetTokenClaims() {
        AccessToken token = AccessToken.reconstruct(
                AccessTokenId.of("123"),
                new TokenClaimsGenerator(),
                "456",
                "789",
                TokenType.Bearer,
                AuthType.Auth,
                java.util.Set.of("read", "write"),
                "secret",
                LocalDateTime.now(),
                3600L,
                7200L,
                TokenStatus.VALID
        );
        
        TokenClaims accessClaims = token.getAccessTokenClaims();
        assertNotNull(accessClaims);
        assertEquals("123", accessClaims.getId());
        assertEquals("456", accessClaims.getSubject());
        assertEquals("789", accessClaims.getIssuer());
        
        TokenClaims refreshClaims = token.getRefreshTokenClaims();
        assertNotNull(refreshClaims);
        assertEquals("123", refreshClaims.getId());
    }

    @Test
    void testVerifyToken() {
        AccessToken token = AccessToken.reconstruct(
                AccessTokenId.of("123"),
                new TokenClaimsGenerator(),
                "456",
                "789",
                TokenType.Bearer,
                AuthType.Auth,
                java.util.Set.of(),
                "secret",
                LocalDateTime.now(),
                3600L,
                7200L,
                TokenStatus.VALID
        );

        String accessToken = token.getAccessToken();
        token.verify(accessToken);
    }

    @Test
    void testIsExpiredWithInvalidStatus() {
        AccessToken token = AccessToken.reconstruct(
                AccessTokenId.of("123"),
                new TokenClaimsGenerator(),
                "456",
                "789",
                TokenType.Bearer,
                AuthType.Auth,
                java.util.Set.of(),
                "secret",
                LocalDateTime.now(),
                3600L,
                7200L,
                TokenStatus.REVOKED
        );

        assertTrue(token.isInvalid());
        assertFalse(token.isExpired());
    }

    @Test
    void testIsExpiredWithExpiredStatus() {
        AccessToken token = AccessToken.reconstruct(
                AccessTokenId.of("123"),
                new TokenClaimsGenerator(),
                "456",
                "789",
                TokenType.Bearer,
                AuthType.Auth,
                java.util.Set.of(),
                "secret",
                LocalDateTime.now().minusSeconds(7200L),
                -3600L,
                7200L,
                TokenStatus.VALID
        );

        assertTrue(token.isExpired());
    }

    @Test
    void testRevokeAlreadyInvalidToken() {
        AccessToken token = AccessToken.reconstruct(
                AccessTokenId.of("123"),
                new TokenClaimsGenerator(),
                "456",
                "789",
                TokenType.Bearer,
                AuthType.Auth,
                java.util.Set.of(),
                "secret",
                LocalDateTime.now(),
                3600L,
                7200L,
                TokenStatus.REVOKED
        );

        // Should not throw
        token.revoke();
        assertEquals(TokenStatus.REVOKED, token.getStatus());
    }

    @Test
    void testValidateWithDefaultValues() {
        // Test validate method with default values
        AccessToken token = AccessToken.create(new TokenClaimsGenerator(), "456", "789", AuthType.Auth, java.util.Set.of(), "secret");

        // Should not throw
        token.validate();
        assertEquals(TokenStatus.VALID, token.getStatus());
    }

    @Test
    void testCreateDefaults() {
        // Test create default values
        AccessToken token = AccessToken.create(new TokenClaimsGenerator(), "456", "789", AuthType.Auth, java.util.Set.of(), "secret");

        assertNotNull(token.getId());
        assertEquals(TokenType.Bearer, token.getTokenType());
        assertEquals(TokenStatus.VALID, token.getStatus());
        assertNotNull(token.getCreateTime());
        assertEquals(7200L, token.getExpireInSeconds());
        assertEquals(604800L, token.getRefreshExpireInSeconds());
        assertTrue(token.getScopes().isEmpty());
    }

    @Test
    void testCreateWithScopes() {
        // Test with custom scopes
        AccessToken token = AccessToken.create(new TokenClaimsGenerator(), "456", "789", AuthType.OAuth, java.util.Set.of("read", "write", "delete"), "secret");

        assertEquals(3, token.getScopes().size());
        assertTrue(token.getScopes().contains("read"));
    }

    @Test
    void testValidateExpiredBranch() {
        // Test the EXPIRED branch in validate()
        // This sets status to EXPIRED when token is expired
        AccessToken token = AccessToken.reconstruct(
                AccessTokenId.of("123"),
                new TokenClaimsGenerator(),
                "456",
                "789",
                TokenType.Bearer,
                AuthType.Auth,
                java.util.Set.of(),
                "secret",
                LocalDateTime.now().minusSeconds(10),
                1L,
                7200L,
                TokenStatus.VALID
        );

        token.validate();
        assertEquals(TokenStatus.EXPIRED, token.getStatus());
    }

    @Test
    void testValidateValidStatusDoesNotChange() {
        // Test validate with VALID status and not expired
        AccessToken token = AccessToken.reconstruct(
                AccessTokenId.of("123"),
                new TokenClaimsGenerator(),
                "456",
                "789",
                TokenType.Bearer,
                AuthType.Auth,
                java.util.Set.of(),
                "secret",
                LocalDateTime.now(),
                3600L,
                7200L,
                TokenStatus.VALID
        );

        token.validate();
        assertEquals(TokenStatus.VALID, token.getStatus());
    }

    @Test
    void testVerifyTokenValidates() {
        // Test verify method
        AccessToken token = AccessToken.reconstruct(
                AccessTokenId.of("123"),
                new TokenClaimsGenerator(),
                "456",
                "789",
                TokenType.Bearer,
                AuthType.Auth,
                java.util.Set.of(),
                "secret",
                LocalDateTime.now(),
                3600L,
                7200L,
                TokenStatus.VALID
        );

        String accessToken = token.getAccessToken();
        token.verify(accessToken);
    }

    @Test
    void testCreateWithDifferentCombinations() {
        // Test different combinations
        // Test 1: With only required fields
        AccessToken token1 = AccessToken.create(new TokenClaimsGenerator(), "456", "789", AuthType.Auth, java.util.Set.of(), "secret");
        assertNotNull(token1);

        // Test 2: Reconstruct with all fields set
        AccessToken token2 = AccessToken.reconstruct(
                AccessTokenId.of("custom-id"),
                new TokenClaimsGenerator(),
                "456",
                "789",
                TokenType.ApiKey,
                AuthType.OAuth,
                java.util.Set.of("scope1"),
                "secret",
                LocalDateTime.now(),
                1800L,
                3600L,
                TokenStatus.REVOKED
        );
        assertNotNull(token2);
        assertEquals(AccessTokenId.of("custom-id"), token2.getId());
        assertEquals(TokenType.ApiKey, token2.getTokenType());
        assertEquals(TokenStatus.REVOKED, token2.getStatus());
    }

    @Test
    void testValidateMethodBranches() {
        // Test validate method to cover all branches
        // Test when status is already INVALID - early return
        AccessToken token1 = AccessToken.reconstruct(
                AccessTokenId.of("123"),
                new TokenClaimsGenerator(),
                "456",
                "789",
                TokenType.Bearer,
                AuthType.Auth,
                java.util.Set.of(),
                "secret",
                LocalDateTime.now(),
                3600L,
                7200L,
                TokenStatus.REVOKED
        );

        token1.validate();
        assertEquals(TokenStatus.REVOKED, token1.getStatus());

        // Test when token is valid and not expired - continues to end
        AccessToken token2 = AccessToken.reconstruct(
                AccessTokenId.of("456"),
                new TokenClaimsGenerator(),
                "789",
                "issuer",
                TokenType.Bearer,
                AuthType.OAuth,
                java.util.Set.of(),
                "secret",
                LocalDateTime.now(),
                3600L,
                7200L,
                TokenStatus.VALID
        );

        token2.validate();
        assertEquals(TokenStatus.VALID, token2.getStatus());
    }

    @Test
    void testAccessTokenCreateWithEmptyScopes() {
        // Test with empty scopes
        AccessToken token = AccessToken.create(new TokenClaimsGenerator(), "user1", "issuer1", AuthType.Auth, java.util.Set.of(), "secret");

        assertNotNull(token.getScopes());
        assertTrue(token.getScopes().isEmpty());
    }
}
