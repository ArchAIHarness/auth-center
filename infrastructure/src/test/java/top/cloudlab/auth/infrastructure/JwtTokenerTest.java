package top.cloudlab.auth.infrastructure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.auth0.jwt.exceptions.JWTVerificationException;

import top.cloudlab.auth.domain.access.TokenClaims;
import top.cloudlab.auth.infrastructure.service.JwtTokener;

class JwtTokenerTest {

    private JwtTokener tokener = new JwtTokener();

    @Test
    void testGenerate() {
        LocalDateTime now = LocalDateTime.now();
        String secret = "test-secret-key";
        TokenClaims claims = TokenClaims.builder()
                .id("token-id-123")
                .subject("user-456")
                .issuer("auth-service")
                .createTime(now)
                .expireInSeconds(3600L)
                .payload(Map.of("scope", "read", "tenantId", "tenant-1"))
                .build();

        String token = tokener.generate(claims, secret);
        assertNotNull(token);
        assertTrue(token.split("\\.").length == 3, "JWT should have 3 parts");
    }

    @Test
    void testParse() {
        LocalDateTime now = LocalDateTime.now();
        String secret = "test-secret-key";
        TokenClaims claims = TokenClaims.builder()
                .id("token-id-123")
                .subject("user-456")
                .issuer("auth-service")
                .createTime(now)
                .expireInSeconds(3600L)
                .payload(Map.of("scope", "read"))
                .build();

        String token = tokener.generate(claims, secret);
        TokenClaims parsed = tokener.parse(token);
        
        assertEquals(claims.getId(), parsed.getId());
        assertEquals(claims.getSubject(), parsed.getSubject());
        assertEquals(claims.getIssuer(), parsed.getIssuer());
    }

    @Test
    void testGenerateWithSameParams() {
        LocalDateTime now = LocalDateTime.now();
        String secret = "123456";
        TokenClaims claims = TokenClaims.builder()
                .id("1")
                .subject("subject")
                .issuer("issuer")
                .createTime(now)
                .expireInSeconds(3600L)
                .payload(Map.of("scope", 1))
                .build();

        String token = tokener.generate(claims, secret);
        String token2 = tokener.generate(claims, secret);
        assertEquals(token2, token, "相同参数多次生成必须token必须一样");
    }

    @Test
    void testValidateWithWrongSecret() {
        LocalDateTime now = LocalDateTime.now();
        String secret = "123456";
        TokenClaims claims = TokenClaims.builder()
                .id("1")
                .subject("subject")
                .issuer("issuer")
                .createTime(now)
                .expireInSeconds(3600L)
                .build();

        String token = tokener.generate(claims, secret);
        
        assertThrows(JWTVerificationException.class,
                () -> tokener.validate(token, "wrong-secret"),
                "秘钥错误，必须抛出 JWTVerificationException 异常");
    }

    @Test
    void testValidateWithCorrectSecret() {
        LocalDateTime now = LocalDateTime.now();
        String secret = "123456";
        TokenClaims claims = TokenClaims.builder()
                .id("1")
                .subject("subject")
                .issuer("issuer")
                .createTime(now)
                .expireInSeconds(3600L)
                .build();

        String token = tokener.generate(claims, secret);
        TokenClaims validated = tokener.validate(token, secret);
        
        assertEquals(claims.getId(), validated.getId());
        assertEquals(claims.getSubject(), validated.getSubject());
    }

    @Test
    void testGenerateWithNullCreateTime() {
        TokenClaims claims = TokenClaims.builder()
                .id("token-id")
                .subject("user-1")
                .issuer("issuer")
                .expireInSeconds(3600L)
                .build();

        String token = tokener.generate(claims, "secret");
        assertNotNull(token);
    }

    @Test
    void testParseWithPayload() {
        LocalDateTime now = LocalDateTime.now();
        Map<String, Object> payload = Map.of(
                "scope", "read,write",
                "tenantId", "tenant-123",
                "role", "admin"
        );
        
        TokenClaims claims = TokenClaims.builder()
                .id("token-id")
                .subject("user-1")
                .issuer("issuer")
                .createTime(now)
                .expireInSeconds(3600L)
                .payload(payload)
                .build();

        String token = tokener.generate(claims, "secret");
        TokenClaims parsed = tokener.parse(token);
        
        assertNotNull(parsed.getPayload());
        assertTrue(parsed.getPayload().containsKey("scope"));
    }
}
