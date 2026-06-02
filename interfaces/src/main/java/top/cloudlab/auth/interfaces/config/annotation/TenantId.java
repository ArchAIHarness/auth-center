package top.cloudlab.auth.interfaces.config.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解，用于获取租户ID
 */
@Target({ ElementType.FIELD, ElementType.PARAMETER }) // 可以应用于字段和参数
@Retention(RetentionPolicy.RUNTIME) // 运行时保留，可通过反射获取
public @interface TenantId {
}
