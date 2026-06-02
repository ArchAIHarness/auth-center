package top.cloudlab.auth.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import top.cloudlab.auth.domain.oauth.GrantType;

class GrantTypeTest {

    @Test
    void testAuthorizationCode() {
        GrantType grantType = GrantType.authorization_code;
        assertNotNull(grantType);
        assertEquals("authorization_code", grantType.name());
    }

    @Test
    void testRefreshToken() {
        GrantType grantType = GrantType.refresh_token;
        assertNotNull(grantType);
        assertEquals("refresh_token", grantType.name());
    }

    @Test
    void testEnumValues() {
        GrantType[] values = GrantType.values();
        assertEquals(2, values.length);
    }

    @Test
    void testValueOf() {
        assertEquals(GrantType.authorization_code, GrantType.valueOf("authorization_code"));
        assertEquals(GrantType.refresh_token, GrantType.valueOf("refresh_token"));
    }
}