package top.cloudlab.auth.infrastructure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import top.cloudlab.auth.common.dto.R;
import top.cloudlab.auth.infrastructure.dto.request.AccessKeyRequest;
import top.cloudlab.auth.infrastructure.dto.response.AccessKeyResponse;
import top.cloudlab.auth.infrastructure.feign.factory.UserFeignClientFallbackFactory;

class FeignFallbackTest {

    @Test
    void testFallbackFactoryCreate() {
        UserFeignClientFallbackFactory factory = new UserFeignClientFallbackFactory();
        RuntimeException cause = new RuntimeException("Service unavailable");
        
        var fallback = factory.create(cause);
        
        assertNotNull(fallback);
    }

    @Test
    void testFallbackValidate() {
        UserFeignClientFallbackFactory factory = new UserFeignClientFallbackFactory();
        RuntimeException cause = new RuntimeException("Service unavailable");
        
        var fallback = factory.create(cause);
        AccessKeyRequest request = AccessKeyRequest.builder()
                .id("ak-123")
                .secret("sk-456")
                .build();
        
        R<AccessKeyResponse> response = fallback.validate(request);
        
        assertNotNull(response);
        assertEquals(500, response.getCode());
        assertEquals("调用用户服务失败", response.getMessage());
    }

    @Test
    void testFallbackWithNullCause() {
        UserFeignClientFallbackFactory factory = new UserFeignClientFallbackFactory();
        
        var fallback = factory.create(null);
        
        assertNotNull(fallback);
    }
}
