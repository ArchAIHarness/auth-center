package top.cloudlab.auth.infrastructure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import top.cloudlab.auth.infrastructure.utils.SnowId;

class SnowIdTest {

    @Test
    void testNext() {
        Long id = SnowId.next();
        assertNotNull(id);
        assertTrue(id > 0);
    }

    @Test
    void testString() {
        String id = SnowId.string();
        assertNotNull(id);
        assertTrue(id.length() > 0);
    }

    @Test
    void testUniqueIds() {
        Set<Long> ids = new HashSet<>();
        for (int i = 0; i < 1000; i++) {
            ids.add(SnowId.next());
        }
        assertEquals(1000, ids.size(), "所有生成的ID应该唯一");
    }

    @Test
    void testIdFormat() {
        Long id = SnowId.next();
        String strId = id.toString();
        assertTrue(strId.length() > 10, "雪花ID应该是一个比较长的数字");
    }

    @Test
    void testMultipleCalls() {
        Long id1 = SnowId.next();
        Long id2 = SnowId.next();
        assertNotEquals(id1, id2, "连续调用应该生成不同的ID");
    }
}
