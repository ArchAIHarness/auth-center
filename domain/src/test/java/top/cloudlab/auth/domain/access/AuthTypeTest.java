package top.cloudlab.auth.domain.access;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class AuthTypeTest {

    @Test
    void testValues() {
        AuthType[] values = AuthType.values();
        assertEquals(3, values.length);
    }

    @Test
    void testValueOf() {
        assertEquals(AuthType.Auth, AuthType.valueOf("Auth"));
        assertEquals(AuthType.OAuth, AuthType.valueOf("OAuth"));
    }
}
