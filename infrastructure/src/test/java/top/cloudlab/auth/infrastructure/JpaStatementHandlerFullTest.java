package top.cloudlab.auth.infrastructure;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import top.cloudlab.auth.infrastructure.jpa.statement.handler.AbstractJpaStatementHandler;
import top.cloudlab.auth.infrastructure.jpa.statement.handler.DeleteJpaStatementHandler;
import top.cloudlab.auth.infrastructure.jpa.statement.handler.PrimaryJpaStatementHandler;
import top.cloudlab.auth.infrastructure.jpa.statement.handler.SelectJpaStatementHandler;

class JpaStatementHandlerFullTest {

    @Test
    void testPrimaryJpaStatementHandlerWithSelect() {
        List<AbstractJpaStatementHandler> handlers = Arrays.asList(
                new SelectJpaStatementHandler(),
                new DeleteJpaStatementHandler()
        );
        PrimaryJpaStatementHandler primaryHandler = new PrimaryJpaStatementHandler(handlers);
        
        String sql = "SELECT id, name FROM users";
        String result = primaryHandler.handle(sql);
        
        assertNotNull(result);
        assertTrue(result.contains("deleted=0"));
    }

    @Test
    void testPrimaryJpaStatementHandlerWithDelete() {
        List<AbstractJpaStatementHandler> handlers = Arrays.asList(
                new SelectJpaStatementHandler(),
                new DeleteJpaStatementHandler()
        );
        PrimaryJpaStatementHandler primaryHandler = new PrimaryJpaStatementHandler(handlers);
        
        String sql = "DELETE FROM users WHERE id = 1";
        String result = primaryHandler.handle(sql);
        
        assertNotNull(result);
        assertTrue(result.contains("deleted=1"));
    }

    @Test
    void testPrimaryJpaStatementHandlerFilterAlwaysTrue() {
        List<AbstractJpaStatementHandler> handlers = Collections.emptyList();
        PrimaryJpaStatementHandler primaryHandler = new PrimaryJpaStatementHandler(handlers);
        
        assertTrue(primaryHandler.filter("any sql"));
    }

    @Test
    void testSelectHandlerWithGroupBy() {
        SelectJpaStatementHandler handler = new SelectJpaStatementHandler();
        
        String sql = "SELECT id, COUNT(*) FROM users GROUP BY id";
        String result = handler.handle(sql);
        
        assertTrue(result.contains("deleted=0"));
    }

    @Test
    void testSelectHandlerWithHaving() {
        SelectJpaStatementHandler handler = new SelectJpaStatementHandler();
        
        String sql = "SELECT id, COUNT(*) as cnt FROM users GROUP BY id HAVING cnt > 1";
        String result = handler.handle(sql);
        
        assertTrue(result.contains("deleted=0"));
    }

    @Test
    void testSelectHandlerWithLimit() {
        SelectJpaStatementHandler handler = new SelectJpaStatementHandler();
        
        String sql = "SELECT id, name FROM users LIMIT 10";
        String result = handler.handle(sql);
        
        assertTrue(result.contains("deleted=0"));
        assertTrue(result.contains("LIMIT"));
    }

    @Test
    void testSelectHandlerWithSubquery() {
        SelectJpaStatementHandler handler = new SelectJpaStatementHandler();
        
        String sql = "SELECT * FROM (SELECT id, name FROM users WHERE active = 1) AS active_users";
        String result = handler.handle(sql);
        
        assertTrue(result.contains("deleted=0"));
    }

    @Test
    void testDeleteHandlerWithAlias() {
        DeleteJpaStatementHandler handler = new DeleteJpaStatementHandler();
        
        String sql = "DELETE u FROM users u WHERE u.id = 1";
        String result = handler.handle(sql);
        
        assertNotNull(result);
    }

    @Test
    void testDeleteHandlerWithAsAlias() {
        DeleteJpaStatementHandler handler = new DeleteJpaStatementHandler();
        
        String sql = "DELETE AS alias FROM users AS alias WHERE alias.id = 1";
        String result = handler.handle(sql);
        
        assertNotNull(result);
    }

    @Test
    void testDeleteHandlerPhysicalDelete() {
        DeleteJpaStatementHandler handler = new DeleteJpaStatementHandler();
        
        String sql = "DELETE FROM users WHERE id = 1 AND physical_delete = true";
        String result = handler.handle(sql);
        
        assertTrue(result.contains("physical_delete"));
    }
}
