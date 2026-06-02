package top.cloudlab.auth.infrastructure.jpa.statement.handler;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * 主JPA语句处理器
 */
@Slf4j
@Primary
@Component
public class PrimaryJpaStatementHandler extends AbstractJpaStatementHandler {

    private final List<AbstractJpaStatementHandler> handlers;

    public PrimaryJpaStatementHandler(List<AbstractJpaStatementHandler> handlers) {
        this.handlers = handlers.stream().filter(handler -> !(handler instanceof PrimaryJpaStatementHandler))
                // 按@Order注解的值排序，值越小优先级越高
                .sorted(Comparator.comparingInt(handler -> {
                    Order order = handler.getClass().getAnnotation(Order.class);
                    return order != null ? order.value() : Integer.MAX_VALUE;
                })).collect(Collectors.toList());
    }

    @Override
    public boolean filter(String sql) {
        return true;
    }

    @Override
    public String handle(String sql) {
        return handlers.stream().reduce(sql, (processedSql, handler) -> {
            if (handler.filter(processedSql)) {
                log.info("Before Translated => {}", processedSql);
                String s = handler.handle(processedSql);
                return s;
            }
            return processedSql;
        }, (a, b) -> b);

    }

}
