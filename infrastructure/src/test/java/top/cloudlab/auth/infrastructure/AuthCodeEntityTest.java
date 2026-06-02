package top.cloudlab.auth.infrastructure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import top.cloudlab.auth.domain.oauth.CodeStatus;
import top.cloudlab.auth.infrastructure.entity.AuthCodeEntity;

class AuthCodeEntityTest {

    @Test
    void testBuilder() {
        AuthCodeEntity entity = AuthCodeEntity.builder()
                .code("auth-code-123")
                .userId("user-456")
                .clientId("client-789")
                .authCreateTime(LocalDateTime.now())
                .expireInSeconds(600)
                .status(CodeStatus.VALID)
                .build();
        
        assertNotNull(entity);
        assertEquals("auth-code-123", entity.getCode());
        assertEquals("user-456", entity.getUserId());
        assertEquals("client-789", entity.getClientId());
        assertEquals(600, entity.getExpireInSeconds());
        assertEquals(CodeStatus.VALID, entity.getStatus());
    }

    @Test
    void testBuilderWithBaseEntityFields() {
        LocalDateTime now = LocalDateTime.now();
        AuthCodeEntity entity = AuthCodeEntity.builder()
                .code("code-123")
                .userId("user-1")
                .clientId("client-1")
                .build();
        entity.setCreateTime(now);
        entity.setModifyTime(now);
        entity.setDeleted(false);
        entity.setVersion(0);
        
        assertNotNull(entity);
    }

    @Test
    void testSetters() {
        AuthCodeEntity entity = new AuthCodeEntity();
        entity.setCode("test-code");
        entity.setUserId("user-1");
        entity.setClientId("client-1");
        entity.setAuthCreateTime(LocalDateTime.now());
        entity.setExpireInSeconds(300);
        entity.setStatus(CodeStatus.USED);
        
        assertNotNull(entity);
        assertEquals("test-code", entity.getCode());
    }

    @Test
    void testDefaultValues() {
        AuthCodeEntity entity = new AuthCodeEntity();
        
        assertNull(entity.getCode());
        assertNull(entity.getUserId());
        assertNull(entity.getClientId());
    }
}
