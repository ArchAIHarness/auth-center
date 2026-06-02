package top.cloudlab.auth.infrastructure;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import top.cloudlab.auth.infrastructure.feign.interceptor.FeignRequestInterceptor;

class FeignRequestInterceptorTest {

    @Test
    void testInterceptorExists() {
        FeignRequestInterceptor interceptor = new FeignRequestInterceptor();
        assertNotNull(interceptor);
    }

    @Test
    void testApplyWithNullTemplate() {
        FeignRequestInterceptor interceptor = new FeignRequestInterceptor();
        
        assertDoesNotThrow(() -> interceptor.apply(null));
    }
}
