package top.cloudlab.auth.domain.access;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class TokenStatusTest {

    @Test
    void testValues() {
        TokenStatus[] values = TokenStatus.values();
        assertEquals(3, values.length);
    }

    @Test
    void testValueOf() {
        assertEquals(TokenStatus.VALID, TokenStatus.valueOf("VALID"));
        assertEquals(TokenStatus.EXPIRED, TokenStatus.valueOf("EXPIRED"));
        assertEquals(TokenStatus.REVOKED, TokenStatus.valueOf("REVOKED"));
    }
}
