package top.cloudlab.auth.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import top.cloudlab.auth.application.dto.command.CreateOAuthTokenRequest;
import top.cloudlab.auth.application.dto.command.RefreshOAuthTokenRequest;
import top.cloudlab.auth.application.dto.response.AccessTokenResponse;
import top.cloudlab.auth.application.service.impl.CreateOAuthTokenService;
import top.cloudlab.auth.application.service.impl.RefreshOAuthTokenService;
import top.cloudlab.auth.common.exception.DomainException;
import top.cloudlab.auth.domain.access.AccessToken;
import top.cloudlab.auth.domain.access.AccessTokenId;
import top.cloudlab.auth.domain.access.AccessTokenRepository;
import top.cloudlab.auth.domain.access.AuthType;
import top.cloudlab.auth.domain.access.TokenClaims;
import top.cloudlab.auth.domain.access.TokenStatus;
import top.cloudlab.auth.domain.access.TokenType;
import top.cloudlab.auth.domain.access.Tokener;
import top.cloudlab.auth.domain.oauth.AuthCode;
import top.cloudlab.auth.domain.oauth.AuthCodeRepository;
import top.cloudlab.auth.domain.oauth.CodeGenerator;
import top.cloudlab.auth.domain.oauth.CodeStatus;
import top.cloudlab.auth.domain.oauth.GrantType;
import top.cloudlab.auth.domain.user.AccessSecret;
import top.cloudlab.auth.domain.user.UserDomainService;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;

class OAuthServiceTest {

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
            Algorithm algorithm = Algorithm.HMAC512(secret);
            JWTVerifier verifier = JWT.require(algorithm).build();
            verifier.verify(token);
            return parse(token);
        }
    }

    public static class TestCodeGenerator implements CodeGenerator {
        @Override
        public String generate(String clientId, String userId) {
            return String.format("%s-%s", clientId, userId);
        }
    }

    private UserDomainService userDomainService;
    private Tokener tokener;
    private AccessTokenRepository accessTokenRepository;
    private AuthCodeRepository authCodeRepository;
    private CreateOAuthTokenService createOAuthTokenService;
    private RefreshOAuthTokenService refreshOAuthTokenService;

    @BeforeEach
    void setUp() {
        userDomainService = mock(UserDomainService.class);
        tokener = new MockTokener();
        accessTokenRepository = mock(AccessTokenRepository.class);
        authCodeRepository = mock(AuthCodeRepository.class);

        createOAuthTokenService = new CreateOAuthTokenService(userDomainService, tokener, accessTokenRepository, authCodeRepository);
        refreshOAuthTokenService = new RefreshOAuthTokenService(userDomainService, tokener, accessTokenRepository);
    }

    @Test
    void testCreateOAuthTokenSuccess() {
        when(userDomainService.validate(any(), any())).thenReturn(Optional.of(
                AccessSecret.builder().userId("user1").ak("client1").sk("sk1").build()));

        AuthCode code = AuthCode.builder()
                .code("client1-user1")
                .userId("user1")
                .clientId("client1")
                .expireInSeconds(600L)
                .createTime(LocalDateTime.now())
                .status(CodeStatus.VALID)
                .build();

        when(authCodeRepository.findFirst(any(), any())).thenReturn(Optional.of(code));

        CreateOAuthTokenRequest request = CreateOAuthTokenRequest.builder()
                .clientId("client1")
                .clientSecret("sk1")
                .code("client1-user1")
                .grantType(GrantType.authorization_code)
                .build();

        AccessTokenResponse response = createOAuthTokenService.generate(request);

        assertNotNull(response);
        assertNotNull(response.getAccessToken());
        assertNotNull(response.getRefreshToken());
        assertEquals("Bearer", response.getTokenType());
    }

    @Test
    void testCreateOAuthTokenInvalidCredentials() {
        when(userDomainService.validate(any(), any())).thenReturn(Optional.empty());

        CreateOAuthTokenRequest request = CreateOAuthTokenRequest.builder()
                .clientId("invalid")
                .clientSecret("invalid")
                .code("code")
                .grantType(GrantType.authorization_code)
                .build();

        assertThrows(DomainException.class, () -> createOAuthTokenService.generate(request));
    }

    @Test
    void testCreateOAuthTokenInvalidCode() {
        when(userDomainService.validate(any(), any())).thenReturn(Optional.of(
                AccessSecret.builder().userId("user1").ak("client1").sk("sk1").build()));
        when(authCodeRepository.findFirst(any(), any())).thenReturn(Optional.empty());

        CreateOAuthTokenRequest request = CreateOAuthTokenRequest.builder()
                .clientId("client1")
                .clientSecret("sk1")
                .code("invalid-code")
                .grantType(GrantType.authorization_code)
                .build();

        assertThrows(DomainException.class, () -> createOAuthTokenService.generate(request));
    }

    @Test
    void testCreateOAuthTokenWrongClientId() {
        when(userDomainService.validate(any(), any())).thenReturn(Optional.of(
                AccessSecret.builder().userId("user1").ak("client1").sk("sk1").build()));

        AuthCode code = AuthCode.builder()
                .code("client1-user1")
                .userId("user1")
                .clientId("client1")
                .expireInSeconds(600L)
                .createTime(LocalDateTime.now())
                .status(CodeStatus.VALID)
                .build();

        when(authCodeRepository.findFirst(any(), any())).thenReturn(Optional.of(code));

        CreateOAuthTokenRequest request = CreateOAuthTokenRequest.builder()
                .clientId("wrong-client")
                .clientSecret("sk1")
                .code("client1-user1")
                .grantType(GrantType.authorization_code)
                .build();

        assertThrows(DomainException.class, () -> createOAuthTokenService.generate(request));
    }

    @Test
    void testRefreshOAuthTokenSuccess() {
        String secret = "test-secret";
        when(userDomainService.validate(any(), any())).thenReturn(Optional.of(
                AccessSecret.builder().userId("user1").ak("client1").sk(secret).build()));

        AccessToken token = AccessToken.builder()
                .id(AccessTokenId.of("token-id-123"))
                .tokener(tokener)
                .userId("user-456")
                .issuerId("client-789")
                .tokenType(TokenType.Bearer)
                .authType(AuthType.OAuth)
                .secret(secret)
                .expireInSeconds(3600L)
                .refreshExpireInSeconds(604800L)
                .createTime(LocalDateTime.now())
                .status(TokenStatus.VALID)
                .build();

        when(accessTokenRepository.findById(any())).thenReturn(Optional.of(token));

        RefreshOAuthTokenRequest request = RefreshOAuthTokenRequest.builder()
                .clientId("client1")
                .clientSecret("sk1")
                .refreshToken(token.getRefreshToken())
                .grantType(GrantType.refresh_token)
                .build();

        AccessTokenResponse response = refreshOAuthTokenService.generate(request);

        assertNotNull(response);
        assertNotNull(response.getAccessToken());
    }

    @Test
    void testRefreshOAuthTokenInvalidCredentials() {
        when(userDomainService.validate(any(), any())).thenReturn(Optional.empty());

        RefreshOAuthTokenRequest request = RefreshOAuthTokenRequest.builder()
                .clientId("invalid")
                .clientSecret("invalid")
                .refreshToken("eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwiaXNzIjoiMTIzNDU2Nzg5MCJ9.invalid")
                .grantType(GrantType.refresh_token)
                .build();

        assertThrows(DomainException.class, () -> refreshOAuthTokenService.generate(request));
    }

    @Test
    void testRefreshOAuthTokenWrongTokenType() {
        String secret = "test-secret";
        when(userDomainService.validate(any(), any())).thenReturn(Optional.of(
                AccessSecret.builder().userId("user1").ak("client1").sk(secret).build()));

        AccessToken token = AccessToken.builder()
                .tokener(tokener)
                .id(AccessTokenId.of("token-id-123"))
                .userId("user-456")
                .issuerId("client-789")
                .tokenType(TokenType.Bearer)
                .authType(AuthType.Auth)
                .secret(secret)
                .expireInSeconds(3600L)
                .build();

        when(accessTokenRepository.findById(any())).thenReturn(Optional.of(token));

        RefreshOAuthTokenRequest request = RefreshOAuthTokenRequest.builder()
                .clientId("client1")
                .clientSecret("sk1")
                .refreshToken(token.getRefreshToken())
                .grantType(GrantType.refresh_token)
                .build();

        assertThrows(DomainException.class, () -> refreshOAuthTokenService.generate(request));
    }
}
