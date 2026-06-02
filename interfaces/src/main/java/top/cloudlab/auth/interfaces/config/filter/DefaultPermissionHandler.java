package top.cloudlab.auth.interfaces.config.filter;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import top.cloudlab.auth.interfaces.config.annotation.PermissionRequires;

/**
 * 默认权限处理器
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class DefaultPermissionHandler {

    private final AbstractPermissionFilter permissionFilter;

    @Around("@annotation(permission)")
    public Object around(ProceedingJoinPoint joinPoint, PermissionRequires permission) throws Throwable {
        if (!permissionFilter.preHandle(joinPoint, permission)) {
            throw new RuntimeException("权限校验失败");
        }
        Object data = joinPoint.proceed();
        return permissionFilter.postHandle(joinPoint, permission, data);
    }

}
