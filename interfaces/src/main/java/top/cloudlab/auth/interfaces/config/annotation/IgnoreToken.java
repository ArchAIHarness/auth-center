package top.cloudlab.auth.interfaces.config.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解，忽略 token 校验
 */
@Target({ ElementType.TYPE, ElementType.METHOD }) // 可以应用于类和方法
@Retention(RetentionPolicy.RUNTIME) // 运行时保留，可通过反射获取
@Documented
public @interface IgnoreToken {
}
