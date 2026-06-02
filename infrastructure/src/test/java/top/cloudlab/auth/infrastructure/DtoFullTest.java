package top.cloudlab.auth.infrastructure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import top.cloudlab.auth.common.dto.R;
import top.cloudlab.auth.infrastructure.dto.request.AccessKeyRequest;
import top.cloudlab.auth.infrastructure.dto.response.AccessKeyResponse;
import top.cloudlab.auth.infrastructure.dto.response.UserInfoResponse;

class DtoFullTest {

    @Test
    void testAccessKeyRequestFullBuilder() {
        AccessKeyRequest request = AccessKeyRequest.builder()
                .id("test-id")
                .secret("test-secret")
                .build();
        
        assertNotNull(request);
        assertEquals("test-id", request.getId());
        assertEquals("test-secret", request.getSecret());
    }

    @Test
    void testAccessKeyRequestAllArgsConstructor() {
        AccessKeyRequest request = new AccessKeyRequest("id", "secret");
        
        assertEquals("id", request.getId());
        assertEquals("secret", request.getSecret());
    }

    @Test
    void testAccessKeyRequestNoArgsConstructor() {
        AccessKeyRequest request = new AccessKeyRequest();
        assertNotNull(request);
    }

    @Test
    void testAccessKeyResponseFull() {
        Set<String> scopes = new HashSet<>();
        scopes.add("read");
        scopes.add("write");
        
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
    void testAccessKeyResponseWithJsonAlias() {
        AccessKeyResponse response = new AccessKeyResponse();
        response.setAk("accessId-alias");
        response.setSk("accessSecret-alias");
        
        assertEquals("accessId-alias", response.getAk());
        assertEquals("accessSecret-alias", response.getSk());
    }

    @Test
    void testAccessKeyResponseAllArgsConstructor() {
        AccessKeyResponse response = new AccessKeyResponse("user-123", "ak", "sk", Set.of("read"));
        
        assertEquals("user-123", response.getUserId());
        assertEquals("ak", response.getAk());
    }

    @Test
    void testAccessKeyResponseNoArgsConstructor() {
        AccessKeyResponse response = new AccessKeyResponse();
        assertNotNull(response);
    }

    @Test
    void testAccessKeyResponseSetters() {
        AccessKeyResponse response = new AccessKeyResponse();
        response.setUserId("user-123");
        response.setAk("ak");
        response.setSk("sk");
        response.setScopes(Set.of("read"));
        
        assertEquals("user-123", response.getUserId());
    }

    @Test
    void testUserInfoResponseFullBuilder() {
        UserInfoResponse response = UserInfoResponse.builder()
                .userId("user-123")
                .nickname("TestUser")
                .phone("13800138000")
                .avatar("http://example.com/avatar.jpg")
                .build();
        
        assertNotNull(response);
        assertEquals("user-123", response.getUserId());
        assertEquals("TestUser", response.getNickname());
        assertEquals("13800138000", response.getPhone());
        assertEquals("http://example.com/avatar.jpg", response.getAvatar());
    }

    @Test
    void testUserInfoResponseAllArgsConstructor() {
        UserInfoResponse response = new UserInfoResponse("user-123", "nickname", "phone", "avatar");
        
        assertEquals("user-123", response.getUserId());
        assertEquals("nickname", response.getNickname());
    }

    @Test
    void testUserInfoResponseNoArgsConstructor() {
        UserInfoResponse response = new UserInfoResponse();
        assertNotNull(response);
    }

    @Test
    void testUserInfoResponseSetters() {
        UserInfoResponse response = new UserInfoResponse();
        response.setUserId("user-123");
        response.setNickname("Test");
        response.setPhone("13800138000");
        response.setAvatar("http://example.com");
        
        assertEquals("user-123", response.getUserId());
    }

    @Test
    void testRFullBuilder() {
        R<String> r = R.<String>builder()
                .code(0)
                .message("success")
                .data("test-data")
                .build();
        
        assertNotNull(r);
        assertEquals(0, r.getCode());
        assertEquals("success", r.getMessage());
        assertEquals("test-data", r.getData());
        assertTrue(r.getSuccess());
    }

    @Test
    void testRBuilderWithNullData() {
        R<String> r = R.<String>builder()
                .code(0)
                .message("success")
                .data(null)
                .build();
        
        assertTrue(r.getSuccess());
    }

    @Test
    void testRBuilderWithNonZeroCode() {
        R<String> r = R.<String>builder()
                .code(500)
                .message("error")
                .build();
        
        assertFalse(r.getSuccess());
    }

    @Test
    void testRNoArgsConstructor() {
        R<String> r = new R<>();
        assertNotNull(r);
    }

    @Test
    void testRAllArgsConstructor() {
        R<String> r = new R<>(200, "error", "data");
        
        assertEquals(200, r.getCode());
        assertEquals("error", r.getMessage());
        assertEquals("data", r.getData());
    }

    @Test
    void testRSetters() {
        R<String> r = new R<>();
        r.setCode(0);
        r.setMessage("success");
        r.setData("data");
        
        assertEquals(0, r.getCode());
    }

    @Test
    void testRWithCodeSet() {
        R<String> r = new R<>();
        r.setCode(200);
        r.setMessage("test");
        
        assertFalse(r.getSuccess());
    }

    @Test
    void testRGetSuccessWithCodeZero() {
        R<String> r = R.<String>builder().code(0).build();
        
        assertTrue(r.getSuccess());
    }

    @Test
    void testRAccessKeyResponse() {
        R<AccessKeyResponse> r = R.<AccessKeyResponse>builder()
                .code(0)
                .data(AccessKeyResponse.builder()
                        .userId("user-123")
                        .ak("ak")
                        .sk("sk")
                        .scopes(Set.of("read"))
                        .build())
                .build();
        
        assertTrue(r.getSuccess());
        assertNotNull(r.getData());
    }

    @Test
    void testRUserInfoResponse() {
        R<UserInfoResponse> r = R.<UserInfoResponse>builder()
                .code(0)
                .data(UserInfoResponse.builder()
                        .userId("user-123")
                        .nickname("Test")
                        .build())
                .build();
        
        assertTrue(r.getSuccess());
        assertNotNull(r.getData());
    }

    @Test
    void testAccessKeyResponseEquals() {
        AccessKeyResponse r1 = AccessKeyResponse.builder()
                .userId("user-123")
                .ak("ak")
                .sk("sk")
                .build();
        
        AccessKeyResponse r2 = r1;
        
        assertEquals(r1, r2);
    }

    @Test
    void testUserInfoResponseToString() {
        UserInfoResponse response = UserInfoResponse.builder()
                .userId("user-123")
                .nickname("TestUser")
                .build();
        
        String str = response.toString();
        assertNotNull(str);
        assertTrue(str.contains("user-123"));
    }

    @Test
    void testAccessKeyResponseToString() {
        AccessKeyResponse response = AccessKeyResponse.builder()
                .userId("user-123")
                .ak("ak")
                .sk("sk")
                .build();
        
        String str = response.toString();
        assertNotNull(str);
    }

    @Test
    void testRToString() {
        R<String> r = R.<String>builder()
                .code(0)
                .message("test")
                .data("data")
                .build();
        
        String str = r.toString();
        assertNotNull(str);
    }
}