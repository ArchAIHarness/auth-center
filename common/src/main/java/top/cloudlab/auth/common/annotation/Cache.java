package top.cloudlab.auth.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 缓存注解
 * 用于标记需要缓存的方法
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Cache {

    /**
     * 缓存键前缀
     *
     * @return 键前缀
     */
    String value() default "";

    /**
     * 过期时间（秒）
     *
     * @return 过期时间
     */
    long expire() default 3600;

    /**
     * 是否使用缓存
     *
     * @return 是否使用缓存
     */
    boolean enabled() default true;
}
