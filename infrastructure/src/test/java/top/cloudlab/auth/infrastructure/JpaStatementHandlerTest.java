package top.cloudlab.auth.infrastructure;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


import org.junit.jupiter.api.Test;

import top.cloudlab.auth.infrastructure.jpa.statement.handler.SelectJpaStatementHandler;
import top.cloudlab.auth.infrastructure.jpa.statement.handler.DeleteJpaStatementHandler;

class JpaStatementHandlerTest {

    @Test
    void testSelectHandlerFilter() {
        SelectJpaStatementHandler handler = new SelectJpaStatementHandler();
        
        assertTrue(handler.filter("SELECT * FROM users"));
        assertTrue(handler.filter("select id from users"));
        assertFalse(handler.filter("INSERT INTO users"));
        assertFalse(handler.filter(""));
        assertFalse(handler.filter(null));
    }

    @Test
    void testSelectHandlerHandleSimpleSelect() {
        SelectJpaStatementHandler handler = new SelectJpaStatementHandler();
        
        String sql = "SELECT id, name FROM users";
        String result = handler.handle(sql);
        
        assertTrue(result.contains("deleted=0"));
    }

    @Test
    void testSelectHandlerHandleWithWhere() {
        SelectJpaStatementHandler handler = new SelectJpaStatementHandler();
        
        String sql = "SELECT id, name FROM users WHERE status = 1";
        String result = handler.handle(sql);
        
        assertTrue(result.contains("deleted=0"));
        assertTrue(result.contains("status = 1"));
    }

    @Test
    void testSelectHandlerHandleWithOrderBy() {
        SelectJpaStatementHandler handler = new SelectJpaStatementHandler();
        
        String sql = "SELECT id, name FROM users ORDER BY id";
        String result = handler.handle(sql);
        
        assertTrue(result.contains("deleted=0"));
        assertTrue(result.contains("ORDER BY"));
    }

    @Test
    void testSelectHandlerHandleWithAlias() {
        SelectJpaStatementHandler handler = new SelectJpaStatementHandler();
        
        String sql = "SELECT u.id, u.name FROM users u";
        String result = handler.handle(sql);
        
        assertTrue(result.contains("u.deleted=0"));
    }

    @Test
    void testSelectHandlerSkipIgnored() {
        SelectJpaStatementHandler handler = new SelectJpaStatementHandler();
        
        assertFalse(handler.filter("SELECT * FROM users ignore_deleted"));
        assertFalse(handler.filter("SELECT * FROM users deleted = 1"));
    }

    @Test
    void testDeleteHandlerFilter() {
        DeleteJpaStatementHandler handler = new DeleteJpaStatementHandler();
        
        assertTrue(handler.filter("DELETE FROM users"));
        assertFalse(handler.filter("SELECT * FROM users"));
    }

    @Test
    void testDeleteHandlerHandle() {
        DeleteJpaStatementHandler handler = new DeleteJpaStatementHandler();
        
        String sql = "DELETE FROM users WHERE id = 1";
        String result = handler.handle(sql);
        
        assertTrue(result.contains("deleted=1"));
    }
}
