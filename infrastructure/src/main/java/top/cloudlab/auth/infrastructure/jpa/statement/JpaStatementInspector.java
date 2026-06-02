package top.cloudlab.auth.infrastructure.jpa.statement;

import java.util.Optional;

import org.hibernate.resource.jdbc.spi.StatementInspector;

import lombok.extern.slf4j.Slf4j;
import top.cloudlab.auth.infrastructure.context.SpringContext;
import top.cloudlab.auth.infrastructure.jpa.statement.handler.AbstractJpaStatementHandler;

/**
 * JPA 语句拦截器
 * 需配置`spring.jpa.properties.hibernate.session_factory.statement_inspector`指向当前类名
 */
@Slf4j
public class JpaStatementInspector implements StatementInspector {

    @Override
    public String inspect(String sql) {
        if (Optional.ofNullable(sql).isEmpty()) {
            return sql;
        }
        AbstractJpaStatementHandler handler = SpringContext.getBean(AbstractJpaStatementHandler.class);
        return Optional.ofNullable(handler)
                .map(h -> h.handle(sql))
                .orElse(sql);
    }

}
