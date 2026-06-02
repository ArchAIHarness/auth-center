package top.cloudlab.auth.infrastructure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import top.cloudlab.auth.domain.access.AuthType;
import top.cloudlab.auth.domain.access.TokenStatus;
import top.cloudlab.auth.domain.access.TokenType;
import top.cloudlab.auth.infrastructure.entity.AccessTokenEntity;

class AccessTokenEntityTest {

    @Test
    void testBuilder() {
        LocalDateTime now = LocalDateTime.now();
        AccessTokenEntity entity = AccessTokenEntity.builder()
                .tokenId("token-123")
                .userId("user-456")
                .issuerId("issuer-789")
                .tokenType(TokenType.Bearer)
                .authType(AuthType.Auth)
                .scopes("read,write")
                .secret("secret-key")
                .tokenCreateTime(now)
                .tokenExpireInSeconds(3600L)
                .refreshExpireInSeconds(604800L)
                .status(TokenStatus.VALID)
                .build();
        
        assertNotNull(entity);
        assertEquals("token-123", entity.getTokenId());
        assertEquals("user-456", entity.getUserId());
        assertEquals("issuer-789", entity.getIssuerId());
        assertEquals(TokenType.Bearer, entity.getTokenType());
        assertEquals(AuthType.Auth, entity.getAuthType());
        assertEquals("read,write", entity.getScopes());
        assertEquals("secret-key", entity.getSecret());
        assertEquals(3600L, entity.getTokenExpireInSeconds());
        assertEquals(604800L, entity.getRefreshExpireInSeconds());
        assertEquals(TokenStatus.VALID, entity.getStatus());
    }

    @Test
    void testBuilderWithBaseEntityFields() {
        LocalDateTime now = LocalDateTime.now();
        AccessTokenEntity entity = AccessTokenEntity.builder()
                .tokenId("token-123")
                .userId("user-1")
                .issuerId("issuer-1")
                .tokenType(TokenType.Bearer)
                .authType(AuthType.OAuth)
                .secret("secret")
                .build();
        entity.setCreateTime(now);
        entity.setModifyTime(now);
        
        assertNotNull(entity);
        assertEquals(now, entity.getCreateTime());
    }

    @Test
    void testSetters() {
        AccessTokenEntity entity = new AccessTokenEntity();
        entity.setTokenId("token-123");
        entity.setUserId("user-456");
        entity.setIssuerId("issuer-789");
        entity.setTokenType(TokenType.Bearer);
        entity.setAuthType(AuthType.Auth);
        entity.setScopes("admin");
        entity.setSecret("secret-key");
        entity.setTokenCreateTime(LocalDateTime.now());
        entity.setTokenExpireInSeconds(7200L);
        entity.setRefreshExpireInSeconds(86400L);
        entity.setStatus(TokenStatus.EXPIRED);
        
        assertEquals("token-123", entity.getTokenId());
        assertEquals("user-456", entity.getUserId());
        assertEquals(TokenStatus.EXPIRED, entity.getStatus());
    }

    @Test
    void testDefaultValues() {
        AccessTokenEntity entity = new AccessTokenEntity();
        
        assertNull(entity.getTokenId());
        assertNull(entity.getUserId());
        assertNull(entity.getIssuerId());
        assertNull(entity.getTokenType());
        assertNull(entity.getAuthType());
    }

    @Test
    void testOAuthToken() {
        AccessTokenEntity entity = AccessTokenEntity.builder()
                .tokenId("oauth-token-123")
                .userId("user-1")
                .issuerId("client-1")
                .authType(AuthType.OAuth)
                .tokenType(TokenType.Bearer)
                .secret("oauth-secret")
                .build();
        
        assertNotNull(entity);
        assertEquals(AuthType.OAuth, entity.getAuthType());
    }
}
