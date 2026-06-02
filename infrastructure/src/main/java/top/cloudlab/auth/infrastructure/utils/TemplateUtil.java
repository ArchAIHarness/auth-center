package top.cloudlab.auth.infrastructure.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 模板工具
 */
public class TemplateUtil {
    private TemplateUtil() {
    }

    /**
     * 模板匹配正则 ${key}
     */
    private static Pattern pattern = Pattern.compile("\\$\\{(.*?)\\}");

    /**
     * 格式化模板 模板格式 ${key}
     * 
     * @param template 模板
     * @param data     数据
     * @return 结果
     */
    public static String render(String template, Map<String, Object> data) {
        Matcher matcher = pattern.matcher(template);
        StringBuilder result = new StringBuilder();
        int lastIndex = 0;

        while (matcher.find()) {
            String key = matcher.group(1).trim();
            Object value = getValueFromMap(data, key);
            result.append(template, lastIndex, matcher.start());
            result.append(value != null ? value : "");
            lastIndex = matcher.end();
        }

        result.append(template.substring(lastIndex));
        return result.toString();
    }

    /**
     * 格式化模板 模板格式 ${1}
     * 
     * @param template 模板
     * @param data     数据
     * @return 结果
     */
    public static String render(String template, List<String> dataList) {
        Matcher matcher = pattern.matcher(template);

        StringBuilder result = new StringBuilder();
        int lastIndex = 0;
        int varIndex = 0;

        while (matcher.find()) {
            /** 提取变量名 */
            String value = "";
            if (dataList.size() > varIndex) {
                value = dataList.get(varIndex++);
            }
            /** 追加变量值数据 */
            result.append(template, lastIndex, matcher.start());
            result.append(value);
            lastIndex = matcher.end();
        }

        /** 添加最后一个匹配变量之后的部分到结果中 */
        result.append(template.substring(lastIndex));

        return result.toString();
    }

    /**
     * 使用对象作为数据源渲染模板
     */
    public static <T> String render(String template, T data) {
        if (data == null) {
            return template;
        }

        Matcher matcher = pattern.matcher(template);
        StringBuilder result = new StringBuilder();
        int lastIndex = 0;

        while (matcher.find()) {
            String key = matcher.group(1).trim();
            Object value = getValueFromObject(data, key);
            result.append(template, lastIndex, matcher.start());
            result.append(value != null ? value : "");
            lastIndex = matcher.end();
        }

        result.append(template.substring(lastIndex));
        return result.toString();
    }

    /**
     * 从对象中获取属性值，支持嵌套属性（如user.id）
     */
    private static Object getValueFromObject(Object obj, String key) {
        String[] keys = key.split("\\.");
        Object current = obj;

        for (String k : keys) {
            current = getFieldValue(current, k);
            if (current == null) {
                break;
            }
        }

        return current;
    }

    /**
     * 从Map中获取值，支持嵌套Map（如user.name）
     */
    private static Object getValueFromMap(Map<String, Object> data, String key) {
        String[] keys = key.split("\\.");
        Object current = data;

        for (String k : keys) {
            if (current instanceof Map) {
                current = ((Map<?, ?>) current).get(k);
                if (current == null) {
                    break;
                }
            } else {
                return null;
            }
        }

        return current;
    }

    /**
     * 获取对象的单个属性值
     */
    @SuppressWarnings("java:S3011")
    private static Object getFieldValue(Object obj, String fieldName) {
        if (obj == null || fieldName == null || fieldName.isEmpty()) {
            return null;
        }

        Class<?> clazz = obj.getClass();

        // 尝试通过getter方法获取
        try {
            String methodName = "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
            Method method = clazz.getMethod(methodName);
            return method.invoke(obj);
        } catch (Exception e) {
            // getter方法不存在，尝试直接访问字段
            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field.get(obj);
            } catch (Exception ex) {
                // 字段也不存在
                return null;
            }
        }
    }

}
