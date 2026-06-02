package top.cloudlab.auth.infrastructure.jpa.statement.handler;

/**
 * 抽象JPA语句处理器
 */
public abstract class AbstractJpaStatementHandler {

    /**
     * 过滤SQL语句
     * 
     * @param sql
     * @return
     */
    public abstract boolean filter(String sql);

    /**
     * 处理SQL语句
     * 
     * @param sql
     * @return
     */
    public abstract String handle(String sql);
}
