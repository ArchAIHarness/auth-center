package top.cloudlab.auth.infrastructure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;

import top.cloudlab.auth.infrastructure.jpa.entity.BaseEntity;

class BaseEntityTest {

    @Test
    void testIdSetter() throws Exception {
        TestBaseEntity entity = new TestBaseEntity();
        entity.setId(1L);
        assertEquals(1L, entity.getId());
    }

    @Test
    void testCreateTime() throws Exception {
        TestBaseEntity entity = new TestBaseEntity();
        var now = java.time.LocalDateTime.now();
        entity.setCreateTime(now);
        assertEquals(now, entity.getCreateTime());
    }

    @Test
    void testModifyTime() throws Exception {
        TestBaseEntity entity = new TestBaseEntity();
        var now = java.time.LocalDateTime.now();
        entity.setModifyTime(now);
        assertEquals(now, entity.getModifyTime());
    }

    @Test
    void testDeleted() throws Exception {
        TestBaseEntity entity = new TestBaseEntity();
        entity.setDeleted(true);
        assertTrue(entity.getDeleted());
        
        entity.setDeleted(false);
        assertFalse(entity.getDeleted());
    }

    @Test
    void testVersion() throws Exception {
        TestBaseEntity entity = new TestBaseEntity();
        entity.setVersion(0);
        assertEquals(0, entity.getVersion());
        
        entity.setVersion(1);
        assertEquals(1, entity.getVersion());
    }

    @Test
    void testOnCreate() throws Exception {
        TestBaseEntity entity = new TestBaseEntity();
        Method onCreate = BaseEntity.class.getDeclaredMethod("onCreate");
        onCreate.setAccessible(true);
        onCreate.invoke(entity);
        
        assertNotNull(entity.getCreateTime());
        assertNotNull(entity.getModifyTime());
        assertEquals(0, entity.getVersion());
        assertEquals(false, entity.getDeleted());
    }

    @Test
    void testOnUpdate() throws Exception {
        TestBaseEntity entity = new TestBaseEntity();
        entity.setCreateTime(java.time.LocalDateTime.now().minusDays(1));
        
        Method onUpdate = BaseEntity.class.getDeclaredMethod("onUpdate");
        onUpdate.setAccessible(true);
        onUpdate.invoke(entity);
        
        assertNotNull(entity.getModifyTime());
    }

    static class TestBaseEntity extends BaseEntity {
    }
}
