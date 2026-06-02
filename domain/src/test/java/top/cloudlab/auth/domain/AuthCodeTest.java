package top.cloudlab.auth.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import top.cloudlab.auth.domain.oauth.AuthCode;
import top.cloudlab.auth.domain.oauth.CodeGenerator;
import top.cloudlab.auth.domain.oauth.CodeStatus;

class AuthCodeTest {

    public class TestCodeGenerator implements CodeGenerator {
        @Override
        public String generate(String clientId, String userId) {
            return String.format("%s-%s", clientId, userId);
        }
    }

    @Test
    void testCreateSuccess() {
        AuthCode authCode = AuthCode.create(new TestCodeGenerator(), "user1", "client1");
        assertNotNull(authCode);
        assertEquals("client1-user1", authCode.getCode());
        assertEquals("user1", authCode.getUserId());
        assertEquals("client1", authCode.getClientId());
        assertEquals(CodeStatus.VALID, authCode.getStatus());
    }

    @Test
    void testCreateWithDefaultValues() {
        AuthCode authCode = AuthCode.create(new TestCodeGenerator(), "user1", "client1");
        assertNotNull(authCode);
        assertEquals(600L, authCode.getExpireInSeconds());
        assertEquals(CodeStatus.VALID, authCode.getStatus());
        assertNotNull(authCode.getCreateTime());
    }

    @Test
    void testValidateSuccess() {
        AuthCode authCode = AuthCode.create(new TestCodeGenerator(), "user1", "client1");
        
        boolean result = authCode.validate("client1");
        assertTrue(result);
        assertEquals(CodeStatus.USED, authCode.getStatus());
    }

    @Test
    void testValidateWrongClientId() {
        AuthCode authCode = AuthCode.create(new TestCodeGenerator(), "user1", "client1");
        
        boolean result = authCode.validate("wrong_client");
        assertFalse(result);
    }

    @Test
    void testValidateAlreadyUsed() {
        AuthCode authCode = AuthCode.reconstruct("client1-user1", "user1", "client1", 600L, LocalDateTime.now(), CodeStatus.USED);
        
        boolean result = authCode.validate("client1");
        assertFalse(result);
        assertEquals(CodeStatus.USED, authCode.getStatus());
    }

    @Test
    void testValidateExpired() {
        AuthCode authCode = AuthCode.reconstruct("client1-user1", "user1", "client1", 1L, LocalDateTime.now().minusSeconds(10), CodeStatus.VALID);
        
        boolean result = authCode.validate("client1");
        assertFalse(result);
        assertEquals(CodeStatus.EXPIRED, authCode.getStatus());
    }

    @Test
    void testReconstructWithPreBuiltCode() {
        // Test the branch where code is already set
        AuthCode authCode = AuthCode.reconstruct("pre-built-code", "user1", "client1", 600L, LocalDateTime.now(), CodeStatus.VALID);
        assertEquals("pre-built-code", authCode.getCode());
    }

    @Test
    void testReconstructStatusExpired() {
        // Test the branch where status is set to VALID and time is already expired
        AuthCode authCode = AuthCode.reconstruct("client1-user1", "user1", "client1", 1L, LocalDateTime.now().minusSeconds(10), CodeStatus.VALID);
        // This should trigger the branch that sets status to EXPIRED in reconstruct
        assertEquals(CodeStatus.EXPIRED, authCode.getStatus());
    }

    @Test
    void testReconstructWithUsedStatus() {
        // Test with USED status directly
        AuthCode authCode = AuthCode.reconstruct("client1-user1", "user1", "client1", 600L, LocalDateTime.now(), CodeStatus.USED);
        assertEquals(CodeStatus.USED, authCode.getStatus());
    }

    @Test
    void testReconstructWithExpiredStatus() {
        // Test with EXPIRED status directly
        AuthCode authCode = AuthCode.reconstruct("client1-user1", "user1", "client1", 600L, LocalDateTime.now(), CodeStatus.EXPIRED);
        assertEquals(CodeStatus.EXPIRED, authCode.getStatus());
    }

    @Test
    void testValidateWithDifferentClientId() {
        // Test validate returns false for different clientId
        AuthCode authCode = AuthCode.create(new TestCodeGenerator(), "user1", "client1");
        
        boolean result = authCode.validate("different-client");
        assertFalse(result);
    }

    @Test
    void testValidateClientIdEquals() {
        // Test the branch where clientId.equals(clientId) returns true
        // This covers the else branch of the first if statement
        AuthCode authCode = AuthCode.create(new TestCodeGenerator(), "user1", "client1");
        
        // This should pass the first if check (!this.clientId.equals(clientId) is false)
        boolean result = authCode.validate("client1");
        assertTrue(result);
        assertEquals(CodeStatus.USED, authCode.getStatus());
    }

    @Test
    void testValidateStatusNotValid() {
        // Test the branch where status is not VALID
        AuthCode authCode = AuthCode.reconstruct("client1-user1", "user1", "client1", 600L, LocalDateTime.now(), CodeStatus.EXPIRED);
        
        boolean result = authCode.validate("client1");
        assertFalse(result);
        // Status should remain EXPIRED
        assertEquals(CodeStatus.EXPIRED, authCode.getStatus());
    }
}
