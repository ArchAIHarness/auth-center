package top.cloudlab.auth.interfaces.config.filter;

import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.context.annotation.Configuration;

import top.cloudlab.auth.interfaces.config.annotation.PermissionRequires;

/**
 * 默认权限过滤器
 */
@Configuration
public class DefaultPermissionFilter implements AbstractPermissionFilter {
    @Override
    public boolean preHandle(ProceedingJoinPoint joinPoint, PermissionRequires permission) {
        return true;
    }

    @Override
    public Object postHandle(ProceedingJoinPoint joinPoint, PermissionRequires permission, Object data) {
        return data;
    }
}
