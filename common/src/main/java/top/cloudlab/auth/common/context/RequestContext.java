package top.cloudlab.auth.common.context;

import java.util.HashMap;
import java.util.Map;

/**
 * 请求上下文
 * 用于在请求处理过程中存储请求头信息
 */
public class RequestContext {

    private static final ThreadLocal<Map<String, String>> contextHolder = new ThreadLocal<>();

    /**
     * 设置请求头
     *
     * @param name  请求头名称
     * @param value 请求头值
     */
    public static void set(String name, String value) {
        Map<String, String> headers = contextHolder.get();
        if (headers == null) {
            headers = new HashMap<>();
            contextHolder.set(headers);
        }
        headers.put(name, value);
    }

    /**
     * 批量设置请求头
     *
     * @param headers 请求头 Map
     */
    public static void setAll(Map<String, String> headers) {
        contextHolder.set(new HashMap<>(headers));
    }

    /**
     * 获取请求头
     *
     * @param name 请求头名称
     * @return 请求头值
     */
    public static String get(String name) {
        Map<String, String> headers = contextHolder.get();
        return headers != null ? headers.get(name) : null;
    }

    /**
     * 获取所有请求头
     *
     * @return 请求头 Map
     */
    public static Map<String, String> getAll() {
        Map<String, String> headers = contextHolder.get();
        return headers != null ? new HashMap<>(headers) : new HashMap<>();
    }

    /**
     * 清除请求上下文
     */
    public static void clear() {
        contextHolder.remove();
    }
}
