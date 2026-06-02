package top.cloudlab.auth.domain.access;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;

class AccessTokenRepositoryTest {

    @Test
    void testInterfaceExists() {
        AccessTokenRepository repository = new AccessTokenRepository() {
            @Override
            public void save(AccessToken token) {
                // no-op
            }

            @Override
            public Optional<AccessToken> findById(AccessTokenId id) {
                return Optional.empty();
            }
        };

        assertNotNull(repository);
    }

    @Test
    void testSaveAndFind() {
        Tokener mockTokener = new Tokener() {
            @Override
            public String generate(TokenClaims claims, String secret) {
                return "test-token-123";
            }

            @Override
            public TokenClaims parse(String token) {
                return null;
            }

            @Override
            public TokenClaims validate(String token, String secret) {
                return null;
            }
        };

        AccessToken testToken = AccessToken.create(mockTokener, "user1", "client1", AuthType.Auth, null, "secret");

        final AccessTokenId tokenId = testToken.getId();

        AccessTokenRepository repository = new AccessTokenRepository() {
            private AccessToken storedToken;

            @Override
            public void save(AccessToken token) {
                this.storedToken = token;
            }

            @Override
            public Optional<AccessToken> findById(AccessTokenId id) {
                return Optional.ofNullable(storedToken).filter(t -> t.getId().equals(id));
            }
        };

        repository.save(testToken);
        Optional<AccessToken> found = repository.findById(tokenId);

        assertTrue(found.isPresent());
        assertEquals("user1", found.get().getUserId());
    }

    @Test
    void testFindByIdNotFound() {
        AccessTokenRepository repository = new AccessTokenRepository() {
            @Override
            public void save(AccessToken token) {
                // no-op
            }

            @Override
            public Optional<AccessToken> findById(AccessTokenId id) {
                return Optional.empty();
            }
        };

        Optional<AccessToken> found = repository.findById(AccessTokenId.generate());

        assertTrue(found.isEmpty());
    }
}
