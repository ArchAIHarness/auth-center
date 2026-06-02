package top.cloudlab.auth.infrastructure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import top.cloudlab.auth.domain.access.AccessToken;
import top.cloudlab.auth.domain.access.AccessTokenId;
import top.cloudlab.auth.domain.access.AccessTokenRepository;
import top.cloudlab.auth.domain.access.AuthType;
import top.cloudlab.auth.domain.access.TokenStatus;
import top.cloudlab.auth.domain.access.TokenType;
import top.cloudlab.auth.domain.access.Tokener;
import top.cloudlab.auth.infrastructure.entity.AccessTokenEntity;
import top.cloudlab.auth.infrastructure.repository.AccessTokenRepositoryImpl;
import top.cloudlab.auth.infrastructure.repository.JPAAccessTokenRepository;
import top.cloudlab.auth.infrastructure.service.JwtTokener;

class AccessTokenRepositoryImplTest {

    private JPAAccessTokenRepository jpaRepository;
    private Tokener tokener;
    private AccessTokenRepository repository;

    @BeforeEach
    void setUp() {
        jpaRepository = mock(JPAAccessTokenRepository.class);
        tokener = new JwtTokener();
        repository = new AccessTokenRepositoryImpl(jpaRepository, tokener);
    }

    @SuppressWarnings("null")
    @Test
    void testSaveNewToken() {
        AccessToken token = AccessToken.reconstruct(
            AccessTokenId.of("token-id-123"),
            tokener,
            "user-456",
            "issuer-789",
            TokenType.Bearer,
            AuthType.Auth,
            Set.of("read", "write"),
            "secret-key",
            LocalDateTime.now(),
            3600L,
            604800L,
            TokenStatus.VALID
        );

        when(jpaRepository.findByTokenId(any())).thenReturn(Optional.empty());
        when(jpaRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        assertNotNull(token);
        repository.save(token);
    }

    @Test
    void testFindByIdFound() {
        AccessTokenEntity entity = AccessTokenEntity.builder()
                .tokenId("token-id-123")
                .userId("user-456")
                .issuerId("issuer-789")
                .tokenType(TokenType.Bearer)
                .authType(AuthType.Auth)
                .scopes("read,write")
                .secret("secret-key")
                .tokenCreateTime(LocalDateTime.now())
                .tokenExpireInSeconds(3600L)
                .refreshExpireInSeconds(604800L)
                .status(TokenStatus.VALID)
                .build();

        when(jpaRepository.findByTokenId("token-id-123")).thenReturn(Optional.of(entity));

        Optional<AccessToken> result = repository.findById(AccessTokenId.of("token-id-123"));

        assertTrue(result.isPresent());
        assertEquals("token-id-123", result.get().getId().value());
        assertEquals("user-456", result.get().getUserId());
    }

    @Test
    void testFindByIdNotFound() {
        when(jpaRepository.findByTokenId("nonexistent")).thenReturn(Optional.empty());

        Optional<AccessToken> result = repository.findById(AccessTokenId.of("nonexistent"));

        assertTrue(result.isEmpty());
    }

    @Test
    void testFindByIdWithEmptyScopes() {
        AccessTokenEntity entity = AccessTokenEntity.builder()
                .tokenId("token-id-123")
                .userId("user-456")
                .issuerId("issuer-789")
                .tokenType(TokenType.Bearer)
                .authType(AuthType.Auth)
                .scopes("")
                .secret("secret-key")
                .tokenCreateTime(LocalDateTime.now())
                .tokenExpireInSeconds(3600L)
                .status(TokenStatus.VALID)
                .build();

        when(jpaRepository.findByTokenId("token-id-123")).thenReturn(Optional.of(entity));

        Optional<AccessToken> result = repository.findById(AccessTokenId.of("token-id-123"));

        assertTrue(result.isPresent());
        assertTrue(result.get().getScopes().isEmpty());
    }
}
