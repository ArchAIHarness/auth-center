package top.cloudlab.auth.infrastructure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import top.cloudlab.auth.common.dto.R;

class RTest {

    @Test
    void testBuilder() {
        R<String> r = R.<String>builder()
                .code(0)
                .message("success")
                .data("test-data")
                .build();
        
        assertNotNull(r);
        assertEquals(0, r.getCode());
        assertEquals("success", r.getMessage());
        assertEquals("test-data", r.getData());
    }

    @Test
    void testGetSuccessWhenCodeIsZero() {
        R<String> r = R.<String>builder()
                .code(0)
                .message("success")
                .build();
        
        assertTrue(r.getSuccess());
    }

    @Test
    void testGetSuccessWhenCodeIsNotZero() {
        R<String> r = R.<String>builder()
                .code(500)
                .message("error")
                .build();
        
        assertFalse(r.getSuccess());
    }

    @Test
    void testSuccessWithNullCode() {
        R<String> r = R.<String>builder()
                .code(0)
                .build();
        
        assertTrue(r.getSuccess());
    }

    @Test
    void testBuilderWithNullData() {
        R<String> r = R.<String>builder()
                .code(0)
                .message("success")
                .data(null)
                .build();
        
        assertNotNull(r);
        assertNull(r.getData());
    }

    @Test
    void testSetters() {
        R<String> r = new R<>();
        r.setCode(200);
        r.setMessage("OK");
        r.setData("test");
        
        assertEquals(200, r.getCode());
        assertEquals("OK", r.getMessage());
        assertEquals("test", r.getData());
    }

    @Test
    void testNoArgsConstructor() {
        R<String> r = new R<>();
        assertNotNull(r);
    }

    @Test
    void testAllArgsConstructor() {
        R<String> r = new R<>(200, "error", "data");
        assertEquals(200, r.getCode());
        assertEquals("error", r.getMessage());
        assertEquals("data", r.getData());
    }
}
