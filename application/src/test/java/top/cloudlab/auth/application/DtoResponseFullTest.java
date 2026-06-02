package top.cloudlab.auth.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Set;

import org.junit.jupiter.api.Test;

import top.cloudlab.auth.application.dto.response.AccessDetailResponse;
import top.cloudlab.auth.application.dto.response.AccessTokenResponse;
import top.cloudlab.auth.application.dto.response.AccessValidatedResponse;
import top.cloudlab.auth.application.dto.response.AuthCodeResponse;
import top.cloudlab.auth.application.dto.response.UserInfoResponse;

class DtoResponseFullTest {

    @Test
    void testAccessTokenResponseAllMethods() {
        AccessTokenResponse r = new AccessTokenResponse();
        r.setTokenType("Bearer");
        r.setAccessToken("access-token");
        r.setTokenExpireInSeconds(3600);
        r.setRefreshToken("refresh-token");
        r.setRefreshExpireInSeconds(604800);

        assertEquals("Bearer", r.getTokenType());
        assertEquals("access-token", r.getAccessToken());
        assertEquals(3600, r.getTokenExpireInSeconds());
        assertEquals("refresh-token", r.getRefreshToken());
        assertEquals(604800, r.getRefreshExpireInSeconds());
        assertNotNull(r.toString());
        assertNotNull(r.hashCode());
    }

    @Test
    void testAccessTokenResponseEquals() {
        AccessTokenResponse r1 = new AccessTokenResponse();
        r1.setAccessToken("token");
        r1.setTokenType("Bearer");
        AccessTokenResponse r2 = new AccessTokenResponse();
        r2.setAccessToken("token");
        r2.setTokenType("Bearer");
        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
    }

    @Test
    void testAccessTokenResponseNotEquals() {
        AccessTokenResponse r1 = new AccessTokenResponse();
        r1.setAccessToken("token1");
        AccessTokenResponse r2 = new AccessTokenResponse();
        r2.setAccessToken("token2");
        assertNotEquals(r1, r2);
    }

    @Test
    @SuppressWarnings("unlikely-arg-type")
    void testAccessTokenResponseEqualsNull() {
        AccessTokenResponse r = new AccessTokenResponse();
        assertFalse(r.equals(null));
        assertFalse(r.equals("string"));
    }

    @Test
    void testAccessDetailResponseAllMethods() {
        AccessDetailResponse r = new AccessDetailResponse();
        r.setUserId("user-123");
        r.setScopes(Set.of("read", "write"));
        r.setAccessToken("access-token");
        r.setRefreshToken("refresh-token");

        assertEquals("user-123", r.getUserId());
        assertEquals(2, r.getScopes().size());
        assertEquals("access-token", r.getAccessToken());
        assertEquals("refresh-token", r.getRefreshToken());
        assertNotNull(r.toString());
        assertNotNull(r.hashCode());
    }

    @Test
    void testAccessDetailResponseEquals() {
        AccessDetailResponse r1 = new AccessDetailResponse();
        r1.setUserId("user-123");
        AccessDetailResponse r2 = new AccessDetailResponse();
        r2.setUserId("user-123");
        assertEquals(r1, r2);
    }

    @Test
    void testAccessDetailResponseNotEquals() {
        AccessDetailResponse r1 = new AccessDetailResponse();
        r1.setUserId("user-1");
        AccessDetailResponse r2 = new AccessDetailResponse();
        r2.setUserId("user-2");
        assertNotEquals(r1, r2);
    }

    @Test
    void testAccessDetailResponseEqualsNull() {
        AccessDetailResponse r = new AccessDetailResponse();
        assertFalse(r.equals(null));
    }

    @Test
    void testUserInfoResponseAllMethods() {
        UserInfoResponse r = new UserInfoResponse();
        r.setUserId("user-123");
        r.setNickname("TestUser");
        r.setAvatar("http://example.com/avatar.png");
        r.setPhone("13800138000");

        assertEquals("user-123", r.getUserId());
        assertEquals("TestUser", r.getNickname());
        assertEquals("http://example.com/avatar.png", r.getAvatar());
        assertEquals("13800138000", r.getPhone());
        assertNotNull(r.toString());
        assertNotNull(r.hashCode());
    }

    @Test
    void testUserInfoResponseEquals() {
        UserInfoResponse r1 = new UserInfoResponse();
        r1.setUserId("user-123");
        UserInfoResponse r2 = new UserInfoResponse();
        r2.setUserId("user-123");
        assertEquals(r1, r2);
    }

    @Test
    void testUserInfoResponseNotEquals() {
        UserInfoResponse r1 = new UserInfoResponse();
        r1.setUserId("user-1");
        UserInfoResponse r2 = new UserInfoResponse();
        r2.setUserId("user-2");
        assertNotEquals(r1, r2);
    }

    @Test
    void testUserInfoResponseEqualsNull() {
        UserInfoResponse r = new UserInfoResponse();
        assertFalse(r.equals(null));
    }

    @Test
    void testAuthCodeResponseAllMethods() {
        AuthCodeResponse r = new AuthCodeResponse();
        r.setClientId("client-123");
        r.setCode("auth-code-456");
        r.setExpireInSeconds(600);

        assertEquals("client-123", r.getClientId());
        assertEquals("auth-code-456", r.getCode());
        assertEquals(600, r.getExpireInSeconds());
        assertNotNull(r.toString());
        assertNotNull(r.hashCode());
    }

    @Test
    void testAuthCodeResponseEquals() {
        AuthCodeResponse r1 = new AuthCodeResponse();
        r1.setCode("code-123");
        AuthCodeResponse r2 = new AuthCodeResponse();
        r2.setCode("code-123");
        assertEquals(r1, r2);
    }

    @Test
    void testAuthCodeResponseNotEquals() {
        AuthCodeResponse r1 = new AuthCodeResponse();
        r1.setCode("code-1");
        AuthCodeResponse r2 = new AuthCodeResponse();
        r2.setCode("code-2");
        assertNotEquals(r1, r2);
    }

    @Test
    void testAuthCodeResponseEqualsNull() {
        AuthCodeResponse r = new AuthCodeResponse();
        assertFalse(r.equals(null));
    }

    @Test
    void testAccessValidatedResponseAllMethods() {
        AccessValidatedResponse r = new AccessValidatedResponse();
        r.setIsValid(true);
        AccessDetailResponse access = AccessDetailResponse.builder()
                .userId("user-123")
                .accessToken("token")
                .build();
        r.setAccess(access);

        assertEquals(true, r.getIsValid());
        assertNotNull(r.getAccess());
        assertEquals("user-123", r.getAccess().getUserId());
        assertNotNull(r.toString());
        assertNotNull(r.hashCode());
    }

    @Test
    void testAccessValidatedResponseEquals() {
        AccessValidatedResponse r1 = new AccessValidatedResponse();
        r1.setIsValid(true);
        AccessValidatedResponse r2 = new AccessValidatedResponse();
        r2.setIsValid(true);
        assertEquals(r1, r2);
    }

    @Test
    void testAccessValidatedResponseNotEquals() {
        AccessValidatedResponse r1 = new AccessValidatedResponse();
        r1.setIsValid(true);
        AccessValidatedResponse r2 = new AccessValidatedResponse();
        r2.setIsValid(false);
        assertNotEquals(r1, r2);
    }

    @Test
    void testAccessValidatedResponseEqualsNull() {
        AccessValidatedResponse r = new AccessValidatedResponse();
        assertFalse(r.equals(null));
    }

    @Test
    void testAccessTokenResponseBuilderAllMethods() {
        AccessTokenResponse r = AccessTokenResponse.builder()
                .tokenType("Bearer")
                .accessToken("access-token")
                .tokenExpireInSeconds(3600)
                .refreshToken("refresh-token")
                .refreshExpireInSeconds(604800)
                .build();

        assertEquals("Bearer", r.getTokenType());
        assertEquals("access-token", r.getAccessToken());
        assertEquals(3600, r.getTokenExpireInSeconds());
        assertEquals("refresh-token", r.getRefreshToken());
        assertEquals(604800, r.getRefreshExpireInSeconds());
    }

    @Test
    void testAccessDetailResponseBuilderAllMethods() {
        AccessDetailResponse r = AccessDetailResponse.builder()
                .userId("user-123")
                .scopes(Set.of("read", "write"))
                .accessToken("access-token")
                .refreshToken("refresh-token")
                .build();

        assertEquals("user-123", r.getUserId());
        assertEquals(2, r.getScopes().size());
        assertEquals("access-token", r.getAccessToken());
        assertEquals("refresh-token", r.getRefreshToken());
    }

    @Test
    void testUserInfoResponseBuilderAllMethods() {
        UserInfoResponse r = UserInfoResponse.builder()
                .userId("user-123")
                .nickname("TestUser")
                .avatar("http://example.com/avatar.png")
                .phone("13800138000")
                .build();

        assertEquals("user-123", r.getUserId());
        assertEquals("TestUser", r.getNickname());
        assertEquals("http://example.com/avatar.png", r.getAvatar());
        assertEquals("13800138000", r.getPhone());
    }

    @Test
    void testAuthCodeResponseBuilderAllMethods() {
        AuthCodeResponse r = AuthCodeResponse.builder()
                .clientId("client-123")
                .code("auth-code-456")
                .expireInSeconds(600)
                .build();

        assertEquals("client-123", r.getClientId());
        assertEquals("auth-code-456", r.getCode());
        assertEquals(600, r.getExpireInSeconds());
    }

    @Test
    void testAccessValidatedResponseBuilderAllMethods() {
        AccessDetailResponse access = AccessDetailResponse.builder()
                .userId("user-123")
                .accessToken("token")
                .build();

        AccessValidatedResponse r = AccessValidatedResponse.builder()
                .isValid(true)
                .access(access)
                .build();

        assertEquals(true, r.getIsValid());
        assertNotNull(r.getAccess());
    }
}
