package top.cloudlab.auth.infrastructure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import top.cloudlab.auth.domain.access.AuthType;
import top.cloudlab.auth.domain.access.TokenStatus;
import top.cloudlab.auth.domain.access.TokenType;
import top.cloudlab.auth.domain.oauth.CodeStatus;
import top.cloudlab.auth.infrastructure.entity.AccessTokenEntity;
import top.cloudlab.auth.infrastructure.entity.AuthCodeEntity;
import top.cloudlab.auth.infrastructure.jpa.entity.BaseEntity;

class EntityFullTest {

    @Test
    void testAccessTokenEntityOnCreate() throws Exception {
        AccessTokenEntity entity = new AccessTokenEntity();
        Method onCreate = BaseEntity.class.getDeclaredMethod("onCreate");
        onCreate.setAccessible(true);
        onCreate.invoke(entity);
        
        assertNotNull(entity.getCreateTime());
        assertNotNull(entity.getModifyTime());
        assertEquals(0, entity.getVersion());
        assertEquals(false, entity.getDeleted());
    }

    @Test
    void testAccessTokenEntityOnUpdate() throws Exception {
        AccessTokenEntity entity = new AccessTokenEntity();
        LocalDateTime originalTime = LocalDateTime.now().minusDays(1);
        entity.setCreateTime(originalTime);
        entity.setModifyTime(originalTime);
        
        Method onUpdate = BaseEntity.class.getDeclaredMethod("onUpdate");
        onUpdate.setAccessible(true);
        onUpdate.invoke(entity);
        
        assertNotNull(entity.getModifyTime());
    }

    @Test
    void testAccessTokenEntityEqualsAndHashCode() {
        AccessTokenEntity entity1 = AccessTokenEntity.builder()
                .tokenId("token-123")
                .userId("user-456")
                .issuerId("issuer-789")
                .tokenType(TokenType.Bearer)
                .authType(AuthType.Auth)
                .secret("secret")
                .build();
        
        AccessTokenEntity entity2 = AccessTokenEntity.builder()
                .tokenId("token-123")
                .userId("user-456")
                .issuerId("issuer-789")
                .tokenType(TokenType.Bearer)
                .authType(AuthType.Auth)
                .secret("secret")
                .build();
        
        assertEquals(entity1, entity2);
        assertEquals(entity1.hashCode(), entity2.hashCode());
    }

    @Test
    void testAccessTokenEntityToString() {
        AccessTokenEntity entity = AccessTokenEntity.builder()
                .tokenId("token-123")
                .userId("user-456")
                .build();
        
        String str = entity.toString();
        assertNotNull(str);
        assertTrue(str.contains("token-123"));
    }

    @Test
    void testAuthCodeEntityOnCreate() throws Exception {
        AuthCodeEntity entity = new AuthCodeEntity();
        Method onCreate = BaseEntity.class.getDeclaredMethod("onCreate");
        onCreate.setAccessible(true);
        onCreate.invoke(entity);
        
        assertNotNull(entity.getCreateTime());
        assertNotNull(entity.getModifyTime());
        assertEquals(0, entity.getVersion());
        assertEquals(false, entity.getDeleted());
    }

    @Test
    void testAuthCodeEntityOnUpdate() throws Exception {
        AuthCodeEntity entity = new AuthCodeEntity();
        LocalDateTime originalTime = LocalDateTime.now().minusDays(1);
        entity.setCreateTime(originalTime);
        entity.setModifyTime(originalTime);
        
        Method onUpdate = BaseEntity.class.getDeclaredMethod("onUpdate");
        onUpdate.setAccessible(true);
        onUpdate.invoke(entity);
        
        assertNotNull(entity.getModifyTime());
    }

    @Test
    void testAuthCodeEntityEqualsAndHashCode() {
        AuthCodeEntity entity1 = AuthCodeEntity.builder()
                .code("code-123")
                .userId("user-456")
                .clientId("client-789")
                .build();
        
        AuthCodeEntity entity2 = AuthCodeEntity.builder()
                .code("code-123")
                .userId("user-456")
                .clientId("client-789")
                .build();
        
        assertEquals(entity1, entity2);
        assertEquals(entity1.hashCode(), entity2.hashCode());
    }

    @Test
    void testAuthCodeEntityToString() {
        AuthCodeEntity entity = AuthCodeEntity.builder()
                .code("code-123")
                .userId("user-456")
                .build();
        
        String str = entity.toString();
        assertNotNull(str);
        assertTrue(str.contains("code-123"));
    }

    @Test
    void testAccessTokenEntityAllFields() {
        LocalDateTime now = LocalDateTime.now();
        AccessTokenEntity entity = AccessTokenEntity.builder()
                .tokenId("token-123")
                .userId("user-456")
                .issuerId("issuer-789")
                .tokenType(TokenType.Bearer)
                .authType(AuthType.Auth)
                .scopes("read,write,admin")
                .secret("secret-key")
                .tokenCreateTime(now)
                .tokenExpireInSeconds(3600L)
                .refreshExpireInSeconds(604800L)
                .status(TokenStatus.VALID)
                .build();
        
        assertEquals("token-123", entity.getTokenId());
        assertEquals("user-456", entity.getUserId());
        assertEquals("issuer-789", entity.getIssuerId());
        assertEquals(TokenType.Bearer, entity.getTokenType());
        assertEquals(AuthType.Auth, entity.getAuthType());
        assertEquals("read,write,admin", entity.getScopes());
        assertEquals("secret-key", entity.getSecret());
        assertEquals(now, entity.getTokenCreateTime());
        assertEquals(3600L, entity.getTokenExpireInSeconds());
        assertEquals(604800L, entity.getRefreshExpireInSeconds());
        assertEquals(TokenStatus.VALID, entity.getStatus());
    }

    @Test
    void testAuthCodeEntityAllFields() {
        LocalDateTime now = LocalDateTime.now();
        AuthCodeEntity entity = AuthCodeEntity.builder()
                .code("auth-code-123")
                .userId("user-456")
                .clientId("client-789")
                .authCreateTime(now)
                .expireInSeconds(600)
                .status(CodeStatus.VALID)
                .build();
        
        assertEquals("auth-code-123", entity.getCode());
        assertEquals("user-456", entity.getUserId());
        assertEquals("client-789", entity.getClientId());
        assertEquals(now, entity.getAuthCreateTime());
        assertEquals(600, entity.getExpireInSeconds());
        assertEquals(CodeStatus.VALID, entity.getStatus());
    }

    @Test
    void testAccessTokenEntityWithNullScopes() {
        AccessTokenEntity entity = AccessTokenEntity.builder()
                .tokenId("token-123")
                .userId("user-456")
                .issuerId("issuer-789")
                .tokenType(TokenType.Bearer)
                .authType(AuthType.Auth)
                .secret("secret")
                .build();
        
        assertEquals(null, entity.getScopes());
    }

    @Test
    void testAuthCodeEntityWithDifferentStatuses() {
        for (CodeStatus status : CodeStatus.values()) {
            AuthCodeEntity entity = AuthCodeEntity.builder()
                    .code("code-" + status)
                    .userId("user-1")
                    .clientId("client-1")
                    .status(status)
                    .build();
            assertEquals(status, entity.getStatus());
        }
    }

    @Test
    void testAccessTokenEntityWithDifferentStatuses() {
        for (TokenStatus status : TokenStatus.values()) {
            AccessTokenEntity entity = AccessTokenEntity.builder()
                    .tokenId("token-" + status)
                    .userId("user-1")
                    .issuerId("issuer-1")
                    .tokenType(TokenType.Bearer)
                    .authType(AuthType.Auth)
                    .secret("secret")
                    .status(status)
                    .build();
            assertEquals(status, entity.getStatus());
        }
    }
}
