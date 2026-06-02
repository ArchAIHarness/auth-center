package top.cloudlab.auth.interfaces.config.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解，用于校验资源权限
 */
@Target({ ElementType.METHOD }) // 可以应用于方法
@Retention(RetentionPolicy.RUNTIME) // 运行时保留，可通过反射获取
@Documented
public @interface PermissionRequires {

    /**
     * 权限范围
     * 
     * @return
     */
    String[] scopes() default {};

    /**
     * 范围参数
     * 
     * @return
     */
    ScopeParam[] params() default {};

    public @interface ScopeParam {
        String name();

        String value();

        boolean required() default true;
    }

}
