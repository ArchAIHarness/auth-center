package top.cloudlab.auth.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import top.cloudlab.auth.application.dto.response.AccessValidatedResponse;
import top.cloudlab.auth.application.dto.response.UserInfoResponse;
import top.cloudlab.auth.application.service.impl.AuthAppServiceImpl;
import top.cloudlab.auth.domain.access.AccessToken;
import top.cloudlab.auth.domain.access.AccessTokenId;
import top.cloudlab.auth.domain.access.AccessTokenRepository;
import top.cloudlab.auth.domain.access.AuthType;
import top.cloudlab.auth.domain.access.TokenClaims;
import top.cloudlab.auth.domain.access.Tokener;
import top.cloudlab.auth.domain.user.UserDomainService;
import top.cloudlab.auth.domain.user.UserInfo;

class AuthAppServiceTest {

    private Tokener tokener;
    private AccessTokenRepository accessTokenRepository;
    private UserDomainService userDomainService;
    private AuthAppServiceImpl authAppService;

    @BeforeEach
    void setUp() {
        tokener = mock(Tokener.class);
        accessTokenRepository = mock(AccessTokenRepository.class);
        userDomainService = mock(UserDomainService.class);
        authAppService = new AuthAppServiceImpl(tokener, accessTokenRepository, userDomainService, "test-secret");
    }

    @Test
    void testVerifyWithValidTokenFromDb() throws Exception {
        String token = "valid_token";
        TokenClaims claims = TokenClaims.builder()
                .id("token-id")
                .subject("user-123")
                .issuer("auth-service")
                .build();

        AccessToken accessToken = AccessToken.builder()
                .id(AccessTokenId.of("token-id"))
                .userId("user-123")
                .issuerId("auth-service")
                .tokener(tokener)
                .secret("secret")
                .authType(AuthType.Auth)
                .scopes(Set.of("read"))
                .build();

        when(tokener.parse(token)).thenReturn(claims);
        when(accessTokenRepository.findById(AccessTokenId.of("token-id"))).thenReturn(Optional.of(accessToken));
        accessToken.verify(token);

        AccessValidatedResponse response = authAppService.verify(token);

        assertTrue(response.getIsValid());
        assertNotNull(response.getAccess());
        assertEquals("user-123", response.getAccess().getUserId());
    }

    @Test
    void testVerifyWithInvalidToken() throws Exception {
        String token = "invalid_token";
        when(tokener.parse(token)).thenThrow(new RuntimeException("Invalid token"));

        AccessValidatedResponse response = authAppService.verify(token);

        assertNotNull(response);
        assertFalse(response.getIsValid());
    }

    @Test
    void testVerifyWithInvalidTokenException() throws Exception {
        // Test exception branch
        String token = "error_token";
        when(tokener.parse(token)).thenThrow(new IllegalArgumentException("Token error"));

        AccessValidatedResponse response = authAppService.verify(token);

        assertNotNull(response);
        assertFalse(response.getIsValid());
    }

    @Test
    void testUserinfoSuccess() {
        String userId = "user-123";
        UserInfo userInfo = UserInfo.builder()
                .userId(userId)
                .nickname("TestUser")
                .phone("13800138000")
                .avatar("http://example.com/avatar.jpg")
                .build();

        when(userDomainService.getUserInfo(userId)).thenReturn(Optional.of(userInfo));

        UserInfoResponse response = authAppService.userinfo(userId);

        assertNotNull(response);
        assertEquals(userId, response.getUserId());
    }

    @Test
    void testVerifyWithTokenFoundInRepository() throws Exception {
        // Test the branch where token is found in repository (optional.isPresent())
        String token = "valid_token";
        TokenClaims claims = TokenClaims.builder()
                .id("token-id")
                .subject("user-123")
                .issuer("auth-service")
                .build();

        AccessToken accessToken = AccessToken.builder()
                .id(AccessTokenId.of("token-id"))
                .userId("user-123")
                .issuerId("auth-service")
                .tokener(tokener)
                .secret("secret")
                .authType(AuthType.Auth)
                .scopes(Set.of("read"))
                .build();

        when(tokener.parse(token)).thenReturn(claims);
        when(accessTokenRepository.findById(AccessTokenId.of("token-id"))).thenReturn(Optional.of(accessToken));

        AccessValidatedResponse response = authAppService.verify(token);

        assertTrue(response.getIsValid());
        assertNotNull(response.getAccess());
    }

    @Test
    void testVerifyWithTokenNotInDbValidateThrowsException() throws Exception {
        // Test branch where optional.isEmpty() and validate throws exception
        String token = "invalid_new_token";
        TokenClaims claims = TokenClaims.builder()
                .id("token-id-2")
                .subject("user-789")
                .issuer("auth-service")
                .build();

        when(tokener.parse(token)).thenReturn(claims);
        when(accessTokenRepository.findById(AccessTokenId.of("token-id-2"))).thenReturn(Optional.empty());
        when(tokener.validate(token, "test-secret")).thenThrow(new RuntimeException("Invalid token"));

        AccessValidatedResponse response = authAppService.verify(token);

        // This covers the catch block when validate throws exception
        assertFalse(response.getIsValid());
    }

    @Test
    void testVerifyWithTokenInDbWithEmptyScopes() throws Exception {
        // Test with token that has empty scopes in AccessToken
        String token = "token_with_empty_scopes";
        TokenClaims claims = TokenClaims.builder()
                .id("token-id")
                .subject("user-123")
                .issuer("auth-service")
                .build();

        // Create AccessToken with empty scopes
        AccessToken accessToken = AccessToken.builder()
                .id(AccessTokenId.of("token-id"))
                .userId("user-123")
                .issuerId("auth-service")
                .tokener(tokener)
                .secret("secret")
                .authType(AuthType.Auth)
                .scopes(Set.of())  // empty scopes
                .build();

        when(tokener.parse(token)).thenReturn(claims);
        when(accessTokenRepository.findById(AccessTokenId.of("token-id"))).thenReturn(Optional.of(accessToken));

        AccessValidatedResponse response = authAppService.verify(token);

        assertTrue(response.getIsValid());
        assertNotNull(response.getAccess());
    }
}