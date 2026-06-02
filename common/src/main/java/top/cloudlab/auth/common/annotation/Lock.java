package top.cloudlab.auth.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 分布式锁注解
 * 用于标记需要加锁的方法
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Lock {

    /**
     * 锁的 key 前缀
     *
     * @return key 前缀
     */
    String value() default "";

    /**
     * 锁的等待时间（秒）
     *
     * @return 等待时间
     */
    long waitTime() default 10;

    /**
     * 锁的持有时间（秒）
     *
     * @return 持有时间
     */
    long leaseTime() default 30;

    /**
     * 是否启用分布式锁
     *
     * @return 是否启用
     */
    boolean enabled() default true;
}
