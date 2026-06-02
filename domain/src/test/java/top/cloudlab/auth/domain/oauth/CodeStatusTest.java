package top.cloudlab.auth.domain.oauth;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class CodeStatusTest {

    @Test
    void testValues() {
        CodeStatus[] values = CodeStatus.values();
        assertEquals(3, values.length);
    }

    @Test
    void testValueOfValid() {
        assertEquals(CodeStatus.VALID, CodeStatus.valueOf("VALID"));
        assertEquals(CodeStatus.USED, CodeStatus.valueOf("USED"));
        assertEquals(CodeStatus.EXPIRED, CodeStatus.valueOf("EXPIRED"));
    }
}
