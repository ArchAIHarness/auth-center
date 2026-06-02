package top.cloudlab.auth.infrastructure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import top.cloudlab.auth.domain.oauth.AuthCode;
import top.cloudlab.auth.domain.oauth.AuthCodeRepository;
import top.cloudlab.auth.domain.oauth.CodeStatus;
import top.cloudlab.auth.infrastructure.entity.AuthCodeEntity;
import top.cloudlab.auth.infrastructure.repository.AuthCodeRepositoryImpl;
import top.cloudlab.auth.infrastructure.repository.JPAAuthCodeRepository;

class AuthCodeRepositoryImplTest {

    private JPAAuthCodeRepository jpaRepository;
    private AuthCodeRepository repository;

    @BeforeEach
    void setUp() {
        jpaRepository = mock(JPAAuthCodeRepository.class);
        repository = new AuthCodeRepositoryImpl(jpaRepository);
    }

    @SuppressWarnings("null")
    @Test
    void testSaveNewCode() {
        AuthCode code = AuthCode.reconstruct(
                "auth-code-123",
                "user-456",
                "client-789",
                600L,
                LocalDateTime.now(),
                CodeStatus.VALID
        );

        when(jpaRepository.findFirstByClientIdAndCodeOrderByIdDesc(any(), any())).thenReturn(Optional.empty());
        when(jpaRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        assertNotNull(code);
        repository.save(code);
    }

    @Test
    void testFindFirstFound() {
        AuthCodeEntity entity = AuthCodeEntity.builder()
                .code("auth-code-123")
                .userId("user-456")
                .clientId("client-789")
                .authCreateTime(LocalDateTime.now())
                .expireInSeconds(600)
                .status(CodeStatus.VALID)
                .build();

        when(jpaRepository.findFirstByClientIdAndCodeOrderByIdDesc("client-789", "auth-code-123"))
                .thenReturn(Optional.of(entity));

        Optional<AuthCode> result = repository.findFirst("client-789", "auth-code-123");

        assertTrue(result.isPresent());
        assertEquals("auth-code-123", result.get().getCode());
        assertEquals("user-456", result.get().getUserId());
    }

    @Test
    void testFindFirstNotFound() {
        when(jpaRepository.findFirstByClientIdAndCodeOrderByIdDesc(any(), any())).thenReturn(Optional.empty());

        Optional<AuthCode> result = repository.findFirst("client-789", "nonexistent");

        assertTrue(result.isEmpty());
    }

    @SuppressWarnings("null")
    @Test
    void testRevokeAll() {
        AuthCodeEntity entity1 = AuthCodeEntity.builder()
                .code("code-1")
                .clientId("client-1")
                .userId("user-1")
                .status(CodeStatus.USED)
                .build();
        AuthCodeEntity entity2 = AuthCodeEntity.builder()
                .code("code-2")
                .clientId("client-1")
                .userId("user-1")
                .status(CodeStatus.USED)
                .build();

        when(jpaRepository.findByClientIdAndUserIdAndStatus("client-1", "user-1", CodeStatus.EXPIRED))
                .thenReturn(Arrays.asList(entity1, entity2));
        when(jpaRepository.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));

        repository.revokeAll("client-1", "user-1");
    }
}
