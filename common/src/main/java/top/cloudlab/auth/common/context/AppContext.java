package top.cloudlab.auth.common.context;

import java.util.HashMap;
import java.util.Map;

/**
 * 应用上下文工具类
 * 用于请求级 ThreadLocal 存储（TraceId、TenantId、UserId 等）
 */
public class AppContext {

    private static final ThreadLocal<Map<String, String>> CONTEXT = new ThreadLocal<>();

    private AppContext() {
    }

    /**
     * 设置上下文值
     *
     * @param key 键
     * @param value 值
     */
    public static void set(String key, String value) {
        if (key == null || value == null) {
            return;
        }
        Map<String, String> context = CONTEXT.get();
        if (context == null) {
            context = new HashMap<>();
            CONTEXT.set(context);
        }
        context.put(key, value);
    }

    /**
     * 获取上下文值
     *
     * @param key 键
     * @return 值
     */
    public static String get(String key) {
        Map<String, String> context = CONTEXT.get();
        return context == null ? null : context.get(key);
    }

    /**
     * 清理上下文
     */
    public static void clear() {
        CONTEXT.remove();
    }
}
