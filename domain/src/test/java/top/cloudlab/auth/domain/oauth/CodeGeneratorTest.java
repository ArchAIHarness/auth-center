package top.cloudlab.auth.domain.oauth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class CodeGeneratorTest {

    @Test
    void testGenerate() {
        CodeGenerator generator = (clientId, userId) -> String.format("%s_%s", clientId, userId);
        
        String code = generator.generate("client123", "user456");
        
        assertNotNull(code);
        assertTrue(code.startsWith("client123_user456"));
    }

    @Test
    void testGenerateDifferentInputs() {
        CodeGenerator generator = (clientId, userId) -> clientId + "-" + userId;
        
        String code1 = generator.generate("client1", "user1");
        String code2 = generator.generate("client2", "user2");
        
        assertEquals("client1-user1", code1);
        assertEquals("client2-user2", code2);
    }
}
