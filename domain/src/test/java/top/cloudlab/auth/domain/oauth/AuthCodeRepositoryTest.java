package top.cloudlab.auth.domain.oauth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;

class AuthCodeRepositoryTest {

    @Test
    void testInterfaceExists() {
        AuthCodeRepository repository = new AuthCodeRepository() {
            @Override
            public void save(AuthCode code) {
                // no-op
            }

            @Override
            public Optional<AuthCode> findFirst(String clientId, String code) {
                return Optional.empty();
            }

            @Override
            public void revokeAll(String clientId, String userId) {
                // no-op
            }
        };
        
        assertNotNull(repository);
    }

    @Test
    void testSaveAndFind() {
        // Create a simple in-memory implementation for testing
        AuthCode testCode = AuthCode.create((c, u) -> "test-code", "user1", "client1");
        
        AuthCodeRepository repository = new AuthCodeRepository() {
            private AuthCode storedCode;
            
            @Override
            public void save(AuthCode code) {
                this.storedCode = code;
            }

            @Override
            public Optional<AuthCode> findFirst(String clientId, String code) {
                return Optional.ofNullable(storedCode);
            }

            @Override
            public void revokeAll(String clientId, String userId) {
                this.storedCode = null;
            }
        };
        
        repository.save(testCode);
        Optional<AuthCode> found = repository.findFirst("client1", "test-code");
        
        assertTrue(found.isPresent());
        assertEquals("user1", found.get().getUserId());
    }

    @Test
    void testRevokeAll() {
        AuthCode testCode = AuthCode.create((c, u) -> "test-code", "user1", "client1");
        
        AuthCodeRepository repository = new AuthCodeRepository() {
            private AuthCode storedCode;
            
            @Override
            public void save(AuthCode code) {
                this.storedCode = code;
            }

            @Override
            public Optional<AuthCode> findFirst(String clientId, String code) {
                return Optional.ofNullable(storedCode);
            }

            @Override
            public void revokeAll(String clientId, String userId) {
                this.storedCode = null;
            }
        };
        
        repository.save(testCode);
        repository.revokeAll("client1", "user1");
        Optional<AuthCode> found = repository.findFirst("client1", "test-code");
        
        assertTrue(found.isEmpty());
    }
}
