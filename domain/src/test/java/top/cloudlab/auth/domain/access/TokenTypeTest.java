package top.cloudlab.auth.domain.access;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class TokenTypeTest {

    @Test
    void testValues() {
        TokenType[] values = TokenType.values();
        assertEquals(3, values.length);
    }

    @Test
    void testValueOf() {
        assertEquals(TokenType.Basic, TokenType.valueOf("Basic"));
        assertEquals(TokenType.Bearer, TokenType.valueOf("Bearer"));
        assertEquals(TokenType.ApiKey, TokenType.valueOf("ApiKey"));
    }
}
