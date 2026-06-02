package top.cloudlab.auth.domain.access;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class TokenerTest {

    @Test
    void testInterfaceExists() {
        // Test that Tokener interface exists and can be implemented
        Tokener tokener = new Tokener() {
            @Override
            public String generate(TokenClaims claims, String secret) {
                return "mock-token";
            }

            @Override
            public TokenClaims parse(String token) {
                return null;
            }

            @Override
            public TokenClaims validate(String token, String secret) {
                return null;
            }
        };
        
        assertNotNull(tokener);
    }

    @Test
    void testGenerate() {
        Tokener tokener = new Tokener() {
            @Override
            public String generate(TokenClaims claims, String secret) {
                return "generated-" + claims.getSubject();
            }

            @Override
            public TokenClaims parse(String token) {
                return null;
            }

            @Override
            public TokenClaims validate(String token, String secret) {
                return null;
            }
        };

        TokenClaims claims = TokenClaims.of(null, "user123", null, null, null, null);

        String token = tokener.generate(claims, "secret");
        assertEquals("generated-user123", token);
    }

    @Test
    void testParse() {
        Tokener tokener = new Tokener() {
            @Override
            public String generate(TokenClaims claims, String secret) {
                return null;
            }

            @Override
            public TokenClaims parse(String token) {
                if (token != null && token.startsWith("Bearer ")) {
                    return TokenClaims.of(null, token.substring(7), null, null, null, null);
                }
                return null;
            }

            @Override
            public TokenClaims validate(String token, String secret) {
                return null;
            }
        };

        TokenClaims claims = tokener.parse("Bearer user123");
        assertNotNull(claims);
        assertEquals("user123", claims.getSubject());
    }
}
