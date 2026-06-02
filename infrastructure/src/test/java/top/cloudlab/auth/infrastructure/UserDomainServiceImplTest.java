package top.cloudlab.auth.infrastructure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import top.cloudlab.auth.common.dto.R;
import top.cloudlab.auth.infrastructure.dto.request.AccessKeyRequest;
import top.cloudlab.auth.infrastructure.dto.response.AccessKeyResponse;
import top.cloudlab.auth.infrastructure.dto.response.UserInfoResponse;
import top.cloudlab.auth.infrastructure.feign.client.LabFeignClient;
import top.cloudlab.auth.infrastructure.feign.client.UserFeignClient;
import top.cloudlab.auth.infrastructure.feign.factory.LabFeignClientFallbackFactory;
import top.cloudlab.auth.infrastructure.service.UserDomainServiceImpl;

import java.util.Set;

class UserDomainServiceImplTest {

    @Test
    void testLabFeignClientFallbackFactory() {
        LabFeignClientFallbackFactory factory = new LabFeignClientFallbackFactory();
        RuntimeException cause = new RuntimeException("Service unavailable");
        
        LabFeignClient client = factory.create(cause);
        
        assertNotNull(client);
        assertEquals(null, client.getUserInfo("user-123", "1"));
    }

    @Test
    void testValidateSuccess() {
        UserFeignClient userFeignClient = mock(UserFeignClient.class);
        LabFeignClient labFeignClient = mock(LabFeignClient.class);
        
        UserDomainServiceImpl service = new UserDomainServiceImpl(userFeignClient, labFeignClient);
        
        AccessKeyRequest request = AccessKeyRequest.builder()
                .id("ak-123")
                .secret("sk-456")
                .build();
        
        R<AccessKeyResponse> response = R.<AccessKeyResponse>builder()
                .code(0)
                .data(AccessKeyResponse.builder()
                        .userId("user-123")
                        .ak("ak-123")
                        .sk("sk-456")
                        .scopes(Set.of("read", "write"))
                        .build())
                .build();
        
        when(userFeignClient.validate(request)).thenReturn(response);
        
        var result = service.validate("ak-123", "sk-456");
        
        assertNotNull(result);
    }

    @Test
    void testValidateFailure() {
        UserFeignClient userFeignClient = mock(UserFeignClient.class);
        LabFeignClient labFeignClient = mock(LabFeignClient.class);
        
        UserDomainServiceImpl service = new UserDomainServiceImpl(userFeignClient, labFeignClient);
        
        R<AccessKeyResponse> response = R.<AccessKeyResponse>builder()
                .code(500)
                .message("error")
                .build();
        
        when(userFeignClient.validate(AccessKeyRequest.builder()
                .id("ak-123")
                .secret("sk-456")
                .build())).thenReturn(response);
        
        try {
            service.validate("ak-123", "sk-456");
        } catch (IllegalArgumentException e) {
            assertEquals("invalid access key or secret", e.getMessage());
        }
    }

    @Test
    void testGetUserInfoSuccess() {
        UserFeignClient userFeignClient = mock(UserFeignClient.class);
        LabFeignClient labFeignClient = mock(LabFeignClient.class);
        
        UserDomainServiceImpl service = new UserDomainServiceImpl(userFeignClient, labFeignClient);
        
        UserInfoResponse response = UserInfoResponse.builder()
                .userId("user-123")
                .nickname("TestUser")
                .phone("13800138000")
                .avatar("http://example.com/avatar.jpg")
                .build();
        
        when(labFeignClient.getUserInfo("user-123", "1")).thenReturn(response);
        
        var result = service.getUserInfo("user-123");
        
        assertNotNull(result);
    }

    @Test
    void testGetUserInfoFailure() {
        UserFeignClient userFeignClient = mock(UserFeignClient.class);
        LabFeignClient labFeignClient = mock(LabFeignClient.class);
        
        UserDomainServiceImpl service = new UserDomainServiceImpl(userFeignClient, labFeignClient);
        
        when(labFeignClient.getUserInfo("user-123", "1")).thenReturn(null);
        
        try {
            service.getUserInfo("user-123");
        } catch (IllegalArgumentException e) {
            assertEquals("invalid user id", e.getMessage());
        }
    }
}