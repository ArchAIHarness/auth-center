package top.cloudlab.auth.infrastructure;

import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class ContextAndInspectorTest {

    @Test
    void testJpaStatementInspectorInspectWithNull() {
        var inspector = new top.cloudlab.auth.infrastructure.jpa.statement.JpaStatementInspector();
        
        String result = inspector.inspect(null);
        
        assertNull(result);
    }
}
