package top.cloudlab.auth.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import top.cloudlab.auth.application.dto.response.AccessDetailResponse;
import top.cloudlab.auth.application.dto.response.AccessTokenResponse;
import top.cloudlab.auth.application.dto.response.AccessValidatedResponse;
import top.cloudlab.auth.application.dto.response.AuthCodeResponse;
import top.cloudlab.auth.application.dto.response.UserInfoResponse;
import top.cloudlab.auth.domain.access.AuthType;
import top.cloudlab.auth.domain.access.TokenClaims;
import top.cloudlab.auth.domain.access.TokenStatus;
import top.cloudlab.auth.domain.access.TokenType;
import top.cloudlab.auth.domain.access.Tokener;
import top.cloudlab.auth.domain.oauth.AuthCode;
import top.cloudlab.auth.domain.oauth.CodeGenerator;
import top.cloudlab.auth.domain.oauth.CodeStatus;
import top.cloudlab.auth.domain.user.UserInfo;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

class ResponseDtoTest {

    public static class TestCodeGenerator implements CodeGenerator {
        @Override
        public String generate(String clientId, String userId) {
            return String.format("%s-%s", clientId, userId);
        }
    }

    public static class MockTokener implements Tokener {
        @Override
        public String generate(TokenClaims claims, String secret) {
            return JWT.create()
                    .withJWTId(claims.getId())
                    .withSubject(claims.getSubject())
                    .withIssuer(claims.getIssuer())
                    .withIssuedAt(claims.getIssuedAt() == null ? new Date().toInstant() : claims.getIssuedAt())
                    .withExpiresAt(claims.getExpiresAt() == null ? null : claims.getExpiresAt())
                    .sign(Algorithm.HMAC512(secret));
        }

        @Override
        public TokenClaims parse(String token) {
            var decoded = JWT.decode(token);
            Map<String, Object> claims = new HashMap<>();
            decoded.getClaims().forEach((key, value) -> claims.put(key, value.as(Object.class)));
            return TokenClaims.builder()
                    .id(decoded.getId())
                    .subject(decoded.getSubject())
                    .issuer(decoded.getIssuer())
                    .createTime(decoded.getIssuedAt() == null ? null
                            : LocalDateTime.ofInstant(decoded.getIssuedAt().toInstant(), ZoneId.systemDefault()))
                    .expireInSeconds(decoded.getExpiresAt() == null || decoded.getIssuedAt() == null ? null
                            : decoded.getExpiresAt().toInstant().getEpochSecond()
                                    - decoded.getIssuedAt().toInstant().getEpochSecond())
                    .build();
        }

        @Override
        public TokenClaims validate(String token, String secret) {
            return parse(token);
        }
    }

    @Test
    void testAuthCodeResponseConvert() {
        AuthCode code = AuthCode.builder()
                .code("client-456-user-123")
                .userId("user-123")
                .clientId("client-456")
                .expireInSeconds(600L)
                .createTime(LocalDateTime.now())
                .status(CodeStatus.VALID)
                .build();

        AuthCodeResponse response = AuthCodeResponse.convert(code);

        assertNotNull(response);
        assertEquals("client-456", response.getClientId());
        assertEquals("client-456-user-123", response.getCode());
        assertEquals(600, response.getExpireInSeconds());
    }

    @Test
    void testAuthCodeResponseBuilder() {
        AuthCodeResponse response = AuthCodeResponse.builder()
                .clientId("client-123")
                .code("auth-code-456")
                .expireInSeconds(300)
                .build();

        assertNotNull(response);
        assertEquals("client-123", response.getClientId());
        assertEquals("auth-code-456", response.getCode());
        assertEquals(300, response.getExpireInSeconds());
    }

    @Test
    void testUserInfoResponseConvert() {
        UserInfo info = UserInfo.builder()
                .userId("user-123")
                .nickname("TestUser")
                .avatar("http://example.com/avatar.png")
                .phone("13800138000")
                .build();

        UserInfoResponse response = UserInfoResponse.convert(info);

        assertNotNull(response);
        assertEquals("user-123", response.getUserId());
        assertEquals("TestUser", response.getNickname());
        assertEquals("http://example.com/avatar.png", response.getAvatar());
        assertEquals("13800138000", response.getPhone());
    }

    @Test
    void testUserInfoResponseBuilder() {
        UserInfoResponse response = UserInfoResponse.builder()
                .userId("user-456")
                .nickname("AnotherUser")
                .phone("13900139000")
                .build();

        assertNotNull(response);
        assertEquals("user-456", response.getUserId());
        assertEquals("AnotherUser", response.getNickname());
    }

    @Test
    void testAccessDetailResponseBuilder() {
        AccessDetailResponse response = AccessDetailResponse.builder()
                .userId("user-123")
                .scopes(Set.of("read", "write"))
                .accessToken("access-token-456")
                .refreshToken("refresh-token-789")
                .build();

        assertNotNull(response);
        assertEquals("user-123", response.getUserId());
        assertEquals(2, response.getScopes().size());
        assertEquals("access-token-456", response.getAccessToken());
        assertEquals("refresh-token-789", response.getRefreshToken());
    }

    @Test
    void testAccessValidatedResponseBuilder() {
        AccessDetailResponse accessDetail = AccessDetailResponse.builder()
                .userId("user-123")
                .accessToken("token")
                .build();

        AccessValidatedResponse response = AccessValidatedResponse.builder()
                .isValid(true)
                .access(accessDetail)
                .build();

        assertNotNull(response);
        assertEquals(true, response.getIsValid());
        assertNotNull(response.getAccess());
        assertEquals("user-123", response.getAccess().getUserId());
    }

    @Test
    void testAccessTokenResponseWithConverter() {
        Tokener tokener = new MockTokener();
        top.cloudlab.auth.domain.access.AccessToken token = top.cloudlab.auth.domain.access.AccessToken.builder()
                .tokener(tokener)
                .id(top.cloudlab.auth.domain.access.AccessTokenId.of("token-123"))
                .userId("user-456")
                .issuerId("issuer-789")
                .tokenType(TokenType.Bearer)
                .authType(AuthType.Auth)
                .secret("secret-key")
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
    void testAccessTokenResponseSetters() {
        AccessTokenResponse response = new AccessTokenResponse();
        response.setTokenType("Bearer");
        response.setAccessToken("test-access-token");
        response.setTokenExpireInSeconds(7200);
        response.setRefreshToken("test-refresh-token");
        response.setRefreshExpireInSeconds(86400);

        assertEquals("Bearer", response.getTokenType());
        assertEquals("test-access-token", response.getAccessToken());
        assertEquals(7200, response.getTokenExpireInSeconds());
        assertEquals("test-refresh-token", response.getRefreshToken());
        assertEquals(86400, response.getRefreshExpireInSeconds());
    }
}
