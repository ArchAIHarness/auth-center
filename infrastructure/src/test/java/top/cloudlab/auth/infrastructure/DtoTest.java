package top.cloudlab.auth.infrastructure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Set;

import org.junit.jupiter.api.Test;

import top.cloudlab.auth.infrastructure.dto.request.AccessKeyRequest;
import top.cloudlab.auth.infrastructure.dto.response.AccessKeyResponse;
import top.cloudlab.auth.infrastructure.dto.response.UserInfoResponse;

class DtoTest {

    @Test
    void testAccessKeyRequestBuilder() {
        AccessKeyRequest request = AccessKeyRequest.builder()
                .id("ak-123")
                .secret("sk-456")
                .build();
        
        assertNotNull(request);
        assertEquals("ak-123", request.getId());
        assertEquals("sk-456", request.getSecret());
    }

    @Test
    void testAccessKeyRequestSetters() {
        AccessKeyRequest request = new AccessKeyRequest();
        request.setId("ak-123");
        request.setSecret("sk-456");
        
        assertEquals("ak-123", request.getId());
        assertEquals("sk-456", request.getSecret());
    }

    @Test
    void testAccessKeyResponseBuilder() {
        Set<String> scopes = Set.of("read", "write");
        AccessKeyResponse response = AccessKeyResponse.builder()
                .userId("user-123")
                .ak("ak-456")
                .sk("sk-789")
                .scopes(scopes)
                .build();
        
        assertNotNull(response);
        assertEquals("user-123", response.getUserId());
        assertEquals("ak-456", response.getAk());
        assertEquals("sk-789", response.getSk());
        assertEquals(2, response.getScopes().size());
    }

    @Test
    void testAccessKeyResponseWithAliases() {
        AccessKeyResponse response = new AccessKeyResponse();
        response.setAk("accessId-alias");
        response.setSk("accessSecret-alias");
        
        assertEquals("accessId-alias", response.getAk());
        assertEquals("accessSecret-alias", response.getSk());
    }

    @Test
    void testAccessKeyResponseWithEmptyScopes() {
        AccessKeyResponse response = AccessKeyResponse.builder()
                .userId("user-123")
                .ak("ak-456")
                .sk("sk-789")
                .scopes(Set.of())
                .build();
        
        assertNotNull(response);
    }

    @Test
    void testUserInfoResponseBuilder() {
        UserInfoResponse response = UserInfoResponse.builder()
                .userId("user-123")
                .nickname("John")
                .phone("13800138000")
                .build();
        
        assertNotNull(response);
        assertEquals("user-123", response.getUserId());
        assertEquals("John", response.getNickname());
        assertEquals("13800138000", response.getPhone());
    }

    @Test
    void testUserInfoResponseSetters() {
        UserInfoResponse response = new UserInfoResponse();
        response.setUserId("user-123");
        response.setNickname("John");
        response.setPhone("13800138000");
        
        assertEquals("user-123", response.getUserId());
    }
}
