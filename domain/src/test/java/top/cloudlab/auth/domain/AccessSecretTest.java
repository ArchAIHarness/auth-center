package top.cloudlab.auth.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import top.cloudlab.auth.domain.user.AccessSecret;
import top.cloudlab.auth.domain.user.UserDomainService;
import top.cloudlab.auth.domain.user.UserInfo;

class AccessSecretTest {

    public class InMemoryUserDomainService implements UserDomainService {

        @Override
        public Optional<AccessSecret> validate(String ak, String sk) {
            if ("valid_ak".equals(ak) && "valid_sk".equals(sk)) {
                return Optional.of(AccessSecret.of("user1", ak, sk, null));
            }
            if ("invalid_ak".equals(ak)) {
                return Optional.of(AccessSecret.of("user2", ak, "wrong_sk", null));
            }
            return Optional.empty();
        }

        @Override
        public Optional<UserInfo> getUserInfo(String userId) {
            if ("user1".equals(userId)) {
                return Optional.of(UserInfo.of(userId, "TestUser", null, "13800138000"));
            }
            return Optional.empty();
        }

        @Override
        public Optional<AccessSecret> accessSecret(String clientId) {
            throw new UnsupportedOperationException("Unimplemented method 'accessSecret'");
        }

        @Override
        public top.cloudlab.auth.domain.user.TenantPermissions getTenantPermissions(String userId) {
            return top.cloudlab.auth.domain.user.TenantPermissions.builder()
                    .tenants(java.util.Set.of())
                    .permissions(java.util.Map.of())
                    .build();
        }

    }

    private UserDomainService userDomainService;

    @BeforeEach
    void setUp() {
        userDomainService = new InMemoryUserDomainService();
    }

    @Test
    void testValidateSuccess() {
        Optional<AccessSecret> accessSecret = userDomainService.validate("valid_ak", "valid_sk");
        assertTrue(accessSecret.isPresent());
        assertEquals("user1", accessSecret.get().getUserId());
        assertEquals("valid_ak", accessSecret.get().getAk());
        assertEquals("valid_sk", accessSecret.get().getSk());
    }

    @Test
    void testValidateFailure() {
        Optional<AccessSecret> accessSecret = userDomainService.validate("invalid_ak", "valid_sk");
        assertTrue(accessSecret.isPresent());
        assertFalse(accessSecret.get().getSk().equals("valid_sk"));
    }

    @Test
    void testValidateNotFound() {
        Optional<AccessSecret> accessSecret = userDomainService.validate("nonexistent_ak", "nonexistent_sk");
        assertTrue(accessSecret.isEmpty());
    }

    @Test
    void testGetUserInfoSuccess() {
        Optional<UserInfo> userInfo = userDomainService.getUserInfo("user1");
        assertTrue(userInfo.isPresent());
        assertEquals("user1", userInfo.get().getUserId());
        assertEquals("TestUser", userInfo.get().getNickname());
        assertEquals("13800138000", userInfo.get().getPhone());
    }

    @Test
    void testGetUserInfoNotFound() {
        Optional<UserInfo> userInfo = userDomainService.getUserInfo("nonexistent_user");
        assertTrue(userInfo.isEmpty());
    }

    @Test
    void testAccessSecretBuilder() {
        AccessSecret accessSecret = AccessSecret.of("test_user", "test_ak", "test_sk", null);
        assertNotNull(accessSecret);
        assertEquals("test_user", accessSecret.getUserId());
        assertEquals("test_ak", accessSecret.getAk());
        assertEquals("test_sk", accessSecret.getSk());
    }
}
