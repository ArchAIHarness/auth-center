package top.cloudlab.auth.infrastructure.jpa.statement.handler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * 查询JPA语句处理器
 */
@Slf4j
@Order(90)
@Component
public class SelectJpaStatementHandler extends AbstractJpaStatementHandler {

    private static final String WHERE_KEYWORD = "where";
    private static final String WHERE_CLAUSE_PREFIX = WHERE_KEYWORD + " ";

    // 增强的SELECT核心结构匹配，确保where不会被当作表别名
    // 优化：支持HAVING，避免不必要的replaceAll，使用非贪婪匹配
    private static final Pattern SELECT_MAIN_PATTERN = Pattern.compile(
            "^select\\s+(.+?)\\s+from\\s+(.+?)(?:\\s+((?:where|group\\s+by|having|order\\s+by|limit)\\b.*))?$",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    // 提取主表引用（处理别名），排除where作为别名
    // 优化：支持引号，支持AS可选
    private static final Pattern FROM_TABLE_PATTERN = Pattern.compile(
            "^\\s*((?:\\(|[`\"\\w]+\\.)?[`\"\\w]+)(?:\\s+(?:as\\s+)?)(?!where\\b)([`\"\\w]+)?\\s*",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    // 子查询别名提取
    private static final Pattern SUBQUERY_ALIAS_PATTERN = Pattern.compile(
            "\\)\\s*(?:as\\s+)?(?!where\\b)([`\"\\w]+)\\s*$",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    // 跳过处理的关键字
    private static final Pattern SKIP_PATTERN = Pattern.compile("ignore_deleted|deleted\\s*=\\s*1",
            Pattern.CASE_INSENSITIVE);

    private static final Pattern WHERE_START_PATTERN = Pattern.compile("^\\s*where\\b", Pattern.CASE_INSENSITIVE);

    @Override
    public boolean filter(String sql) {
        if (!StringUtils.hasText(sql)) {
            return false;
        }
        String trimmedSql = sql.trim();
        // 快速检查前缀，避免正则开销
        if (!StringUtils.startsWithIgnoreCase(trimmedSql, "select")) {
            return false;
        }
        return !SKIP_PATTERN.matcher(trimmedSql).find();
    }

    @Override
    public String handle(String sql) {
        // 直接匹配，避免replaceAll产生的临时字符串开销
        Matcher mainMatcher = SELECT_MAIN_PATTERN.matcher(sql.trim());
        if (!mainMatcher.find()) {
            log.warn("SELECT语句格式不匹配，跳过处理: [{}]", sql);
            return sql;
        }

        // 提取SELECT各部分
        String selectColumns = mainMatcher.group(1).trim();
        String fromPart = mainMatcher.group(2).trim();
        String afterFrom = mainMatcher.group(3) != null ? mainMatcher.group(3).trim() : "";

        // 解析主表引用
        String mainTableRef = parseTableReference(fromPart);

        // 构建 deleted = 0 条件
        String deletedCondition = null == mainTableRef ? "deleted=0" : mainTableRef + ".deleted=0";

        // 处理FROM之后的部分
        String processedAfterFrom = processAfterFrom(afterFrom, deletedCondition);

        // 组装最终SQL
        // 使用StringBuilder减少字符串拼接开销
        StringBuilder finalSql = new StringBuilder();
        finalSql.append("select ").append(selectColumns)
                .append(" from ").append(fromPart)
                .append(" ").append(processedAfterFrom);

        return finalSql.toString().replaceAll("\\s+", " ").trim();
    }

    private String parseTableReference(String fromPart) {
        // 处理子查询
        if (fromPart.startsWith("(") && fromPart.contains(")")) {
            Matcher subMatcher = SUBQUERY_ALIAS_PATTERN.matcher(fromPart);
            if (subMatcher.find()) {
                return subMatcher.group(1).trim();
            }
            // 递归解析子查询
            String subSql = fromPart.substring(1, fromPart.lastIndexOf(")")).trim();
            Matcher subMainMatcher = SELECT_MAIN_PATTERN.matcher(subSql);
            if (subMainMatcher.find()) {
                return parseTableReference(subMainMatcher.group(2).trim());
            }
            return null;
        }

        // 处理普通表
        Matcher tableMatcher = FROM_TABLE_PATTERN.matcher(fromPart);
        if (tableMatcher.find()) {
            String tableName = tableMatcher.group(1).trim();
            String alias = tableMatcher.group(2);

            if (StringUtils.hasText(alias)) {
                return alias.trim();
            } else {
                return tableName.contains(".") ? tableName.substring(tableName.lastIndexOf(".") + 1).trim() : tableName;
            }
        }

        return null;
    }

    /**
     * 处理FROM之后的所有内容，正确添加deleted条件
     */
    private String processAfterFrom(String afterFrom, String deletedCondition) {
        if (!StringUtils.hasText(afterFrom)) {
            return WHERE_CLAUSE_PREFIX + deletedCondition;
        }

        // 检查是否包含WHERE子句（使用单词边界确保精确匹配）
        Matcher whereMatcher = WHERE_START_PATTERN.matcher(afterFrom);
        if (whereMatcher.find()) {
            // 提取WHERE后面的条件部分
            int whereEndIndex = whereMatcher.end();
            String originalWhereConditions = afterFrom.substring(whereEndIndex).trim();

            // 已存在WHERE，在原有条件前添加deleted条件
            return WHERE_CLAUSE_PREFIX + deletedCondition
                    + (StringUtils.hasText(originalWhereConditions) ? " and " + originalWhereConditions : "");
        } else {
            // 不存在WHERE，先添加WHERE和deleted条件，再拼接其他内容
            return WHERE_CLAUSE_PREFIX + deletedCondition + " " + afterFrom;
        }
    }

}
