package top.cloudlab.auth.infrastructure.jpa.statement.handler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * 删除JPA语句处理器
 */
@Slf4j
@Order(99)
@Component
public class DeleteJpaStatementHandler extends AbstractJpaStatementHandler {
    // 匹配DELETE语句的正则表达式，支持两种格式：
    // 1. delete from table where ...
    // 2. delete t from table t where ...
    // 优化：支持引号，支持AS关键字，避免不必要的replaceAll
    private static final Pattern DELETE_PATTERN = Pattern.compile(
            "^delete\\s+(?:([`\"\\w]+)\\s+)?from\\s+([`\"\\w\\.]+)(?:\\s+(?:as\\s+)?(?!where\\b)([`\"\\w]+))?\\s*(where\\s+.*)?$",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    // 跳过处理的关键字（忽略大小写）
    private static final Pattern SKIP_PATTERN = Pattern.compile("force_delete|physical_delete",
            Pattern.CASE_INSENSITIVE);

    @Override
    public boolean filter(String sql) {
        if (!StringUtils.hasText(sql)) {
            return false;
        }
        String trimmedSql = sql.trim();
        // 快速检查前缀，避免正则开销
        if (!StringUtils.startsWithIgnoreCase(trimmedSql, "delete")) {
            return false;
        }
        return !SKIP_PATTERN.matcher(trimmedSql).find();
    }

    @Override
    public String handle(String sql) {
        // 直接匹配，避免replaceAll产生的临时字符串开销
        Matcher matcher = DELETE_PATTERN.matcher(sql.trim());
        if (!matcher.find()) {
            // 如果正则不匹配，返回原SQL（或者根据需求抛出异常）
            log.warn("DELETE语句格式不匹配，跳过处理: [{}]", sql);
            return sql;
        }
        return rebuildStatement(sql, matcher);
    }

    /**
     * 重构语句
     */
    private String rebuildStatement(String originalSql, Matcher matcher) {
        // 提取匹配组
        String deleteAlias = matcher.group(1); // 对应格式2中的别名t
        String tableName = matcher.group(2); // 表名
        String tableAlias = matcher.group(3); // 表别名
        String whereClause = matcher.group(4); // WHERE子句

        // 确定要使用的别名（优先使用表别名）
        String aliasToUse = StringUtils.hasText(tableAlias) ? tableAlias : deleteAlias;

        // 构建UPDATE语句
        StringBuilder updateSql = new StringBuilder();
        updateSql.append("update ").append(tableName);

        // 处理表别名
        if (StringUtils.hasText(aliasToUse)) {
            updateSql.append(" ").append(aliasToUse);
        }

        // 设置删除标志（根据是否有别名决定格式）
        String deletedColumn = StringUtils.hasText(aliasToUse) ? aliasToUse + ".deleted=1" : "deleted=1";
        updateSql.append(" set ").append(deletedColumn);

        // 处理WHERE子句
        if (StringUtils.hasText(whereClause)) {
            updateSql.append(" ").append(whereClause);
        }

        // 仅在最后进行一次空格规范化
        return updateSql.toString().replaceAll("\\s+", " ").trim();
    }

}
