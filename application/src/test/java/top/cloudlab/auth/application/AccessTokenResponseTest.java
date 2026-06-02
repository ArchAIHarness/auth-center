package top.cloudlab.auth.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import top.cloudlab.auth.application.dto.response.AccessTokenResponse;
import top.cloudlab.auth.domain.access.AccessToken;
import top.cloudlab.auth.domain.access.AccessTokenId;
import top.cloudlab.auth.domain.access.AuthType;
import top.cloudlab.auth.domain.access.TokenClaims;
import top.cloudlab.auth.domain.access.TokenStatus;
import top.cloudlab.auth.domain.access.TokenType;
import top.cloudlab.auth.domain.access.Tokener;

class AccessTokenResponseTest {

    public static class MockTokener implements Tokener {

        @Override
        public String generate(TokenClaims claims, String secret) {
            return "mock_token_" + claims.getId();
        }

        @Override
        public TokenClaims parse(String token) {
            return TokenClaims.builder()
                    .id("mock-id")
                    .subject("user-1")
                    .issuer("issuer-1")
                    .createTime(LocalDateTime.now())
                    .expireInSeconds(3600L)
                    .build();
        }

        @Override
        public TokenClaims validate(String token, String secret) {
            return parse(token);
        }
    }

    @Test
    void testConvert() {
        AccessToken token = AccessToken.builder()
                .tokener(new MockTokener())
                .id(AccessTokenId.of("token-123"))
                .userId("user-456")
                .issuerId("issuer-789")
                .tokenType(TokenType.Bearer)
                .authType(AuthType.Auth)
                .secret("secret")
                .expireInSeconds(3600L)
                .refreshExpireInSeconds(604800L)
                .createTime(LocalDateTime.now())
                .status(TokenStatus.VALID)
                .build();
        
        AccessTokenResponse response = AccessTokenResponse.convert(token);
        
        assertNotNull(response);
        assertEquals("Bearer", response.getTokenType());
        assertNotNull(response.getAccessToken());
        assertEquals(3600, response.getTokenExpireInSeconds());
        assertNotNull(response.getRefreshToken());
        assertEquals(604800, response.getRefreshExpireInSeconds());
    }

    @Test
    void testBuilder() {
        AccessTokenResponse response = AccessTokenResponse.builder()
                .tokenType("Bearer")
                .accessToken("access_token_123")
                .tokenExpireInSeconds(3600)
                .refreshToken("refresh_token_456")
                .refreshExpireInSeconds(604800)
                .build();
        
        assertNotNull(response);
        assertEquals("Bearer", response.getTokenType());
        assertEquals("access_token_123", response.getAccessToken());
        assertEquals(3600, response.getTokenExpireInSeconds());
        assertEquals("refresh_token_456", response.getRefreshToken());
        assertEquals(604800, response.getRefreshExpireInSeconds());
    }
}
