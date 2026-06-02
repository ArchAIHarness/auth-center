package top.cloudlab.auth.common.context;

import java.util.Optional;

/**
 * 用户上下文
 * 用于获取当前登录用户信息
 */
public class UserContext {

    private UserContext() {
    }

    private static final ThreadLocal<String> userIdHolder = new ThreadLocal<>();
    private static final ThreadLocal<String> tenantIdHolder = new ThreadLocal<>();

    /**
     * 设置当前用户 ID
     *
     * @param userId 用户 ID
     */
    public static void setUserId(String userId) {
        userIdHolder.set(userId);
    }

    /**
     * 获取当前用户 ID
     *
     * @return 用户 ID
     */
    public static String getUserId() {
        return userIdHolder.get();
    }

    /**
     * 获取当前用户 ID（Optional）
     *
     * @return Optional 包装的用户 ID
     */
    public static Optional<String> getUserIdOptional() {
        return Optional.ofNullable(userIdHolder.get());
    }

    /**
     * 设置当前租户 ID
     *
     * @param tenantId 租户 ID
     */
    public static void setTenantId(String tenantId) {
        tenantIdHolder.set(tenantId);
    }

    /**
     * 获取当前租户 ID
     *
     * @return 租户 ID
     */
    public static String getTenantId() {
        return tenantIdHolder.get();
    }

    /**
     * 获取当前租户 ID（Optional）
     *
     * @return Optional 包装的租户 ID
     */
    public static Optional<String> getTenantIdOptional() {
        return Optional.ofNullable(tenantIdHolder.get());
    }

    /**
     * 清除当前用户上下文
     */
    public static void clear() {
        userIdHolder.remove();
        tenantIdHolder.remove();
    }
}
