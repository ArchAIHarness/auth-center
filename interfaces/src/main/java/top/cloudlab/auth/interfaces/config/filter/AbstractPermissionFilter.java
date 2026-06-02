package top.cloudlab.auth.interfaces.config.filter;

import org.aspectj.lang.ProceedingJoinPoint;

import top.cloudlab.auth.interfaces.config.annotation.PermissionRequires;

/**
 * 抽象权限过滤器
 */
public interface AbstractPermissionFilter {

    /**
     * 前置处理
     * 
     * @param joinPoint  连接点
     * @param permission 资源权限
     * @return 是否通过
     */
    default boolean preHandle(ProceedingJoinPoint joinPoint, PermissionRequires permission) {
        return true;
    }

    /**
     * 后置处理
     * 
     * @param joinPoint
     * @param permission
     * @param data
     * @return
     */
    default Object postHandle(ProceedingJoinPoint joinPoint, PermissionRequires permission, Object data) {
        return data;
    }

}
