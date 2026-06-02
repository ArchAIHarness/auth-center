package top.cloudlab.auth.infrastructure.utils;

import java.lang.reflect.Method;
import java.util.Objects;

import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.lang.NonNull;
import org.springframework.util.ObjectUtils;

/**
 * SpEL 表达式工具类
 */
public class SpELUtil {

    private SpELUtil() {
        // 防止实例化
    }

    /**
     * SpEL 表达式占位符
     */
    private static final String PLACEHOLDER = "#";

    /**
     * SpEL 表达式解析器
     */
    private final static SpelExpressionParser parser = new SpelExpressionParser();

    /**
     * SpEL 表达式参数名发现器
     */
    private final static ParameterNameDiscoverer nameDiscoverer = new DefaultParameterNameDiscoverer();

    /**
     * 获取 SpEL 表达式解析结果
     * 
     * @param expression SpEL 表达式
     * @return 解析结果
     */
    public static <T> T get(@NonNull String expression, @NonNull Method method, Object[] args, Class<T> clazz) {
        Objects.requireNonNull(clazz, "clazz must not be null");

        if (!expression.contains(PLACEHOLDER)) {
            return convertLiteral(expression, clazz);
        }

        // 1. 获取参数名（解析失败则生成默认名 param0/param1...）
        String[] paramNames = nameDiscoverer.getParameterNames(method);
        if (ObjectUtils.isEmpty(paramNames)) {
            paramNames = generateDefaultParamNames(args);
        }

        // 2. 解析表达式
        Expression parsedExpression = parser.parseExpression(expression);
        StandardEvaluationContext context = new StandardEvaluationContext();

        if (paramNames == null || args == null) {
            return null;
        }

        // 3. 绑定参数（兼容参数名/值长度不匹配，避免数组越界）
        int bindLength = Math.min(paramNames.length, args == null ? 0 : args.length);
        for (int i = 0; i < bindLength; i++) {
            context.setVariable(paramNames[i], args[i]);
            context.setVariable("p" + i, args[i]);
            context.setVariable("a" + i, args[i]);
            context.setVariable("arg" + i, args[i]);
        }

        if (args != null) {
            context.setVariable("args", args);
        }

        // 4. 解析表达式（失败时保证不中断）
        try {
            return parsedExpression.getValue(context, clazz);
        } catch (Exception e) {
            if (String.class.equals(clazz)) {
                return clazz.cast(expression);
            }
            return null;
        }
    }

    private static String[] generateDefaultParamNames(Object[] args) {
        int length = args == null ? 0 : args.length;
        String[] names = new String[length];
        for (int i = 0; i < length; i++) {
            names[i] = "param" + i;
        }
        return names;
    }

    private static <T> T convertLiteral(String raw, @NonNull Class<T> clazz) {
        if (raw == null) {
            return null;
        }
        if (String.class.equals(clazz)) {
            return clazz.cast(raw);
        }

        ConversionService conversionService = DefaultConversionService.getSharedInstance();
        if (conversionService.canConvert(String.class, clazz)) {
            return conversionService.convert(raw, clazz);
        }

        return null;
    }

}
