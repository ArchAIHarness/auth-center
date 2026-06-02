package top.cloudlab.auth.interfaces.config.interceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import top.cloudlab.auth.common.context.UserContext;
import top.cloudlab.auth.interfaces.config.annotation.IgnoreToken;
import top.cloudlab.auth.interfaces.config.exception.UnauthorizedException;

/**
 * Token 拦截器
 * 验证用户身份，从 UserContext 读取当前用户信息
 */
@Slf4j
@Configuration
public class TokenInterceptor implements HandlerInterceptor, WebMvcConfigurer {
    
    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
            @NonNull Object handler)
            throws Exception {
        String requestURI = request.getRequestURI();
        log.info("拦截到请求：{}", requestURI);

        // 1. 排除非 Controller 方法（如静态资源）
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        
        // 2. 判断类或方法是否注册 @IgnoreToken
        boolean hasIgnoreToken = handlerMethod.getBeanType().isAnnotationPresent(IgnoreToken.class)
                || handlerMethod.getMethod().isAnnotationPresent(IgnoreToken.class);
        if (hasIgnoreToken) {
            // 有注解，跳过 Token 校验
            return true;
        }
        
        // 3. 从 UserContext 获取当前用户 ID（由 AppContextFilter 设置）
        String userId = UserContext.getUserId();
        if (userId == null || userId.trim().isEmpty()) {
            log.error("用户身份验证失败，请求URI: {}", requestURI);
            throw new UnauthorizedException("无效访问身份，请先进行登录");
        }
        
        // 4. 有用户信息则放行
        log.info("用户身份验证成功，userId: {}", userId);
        return true;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
            @NonNull Object handler, @Nullable Exception ex) {
        // 上下文清理由 AppFilter 统一处理，此处无需重复清理
    }

    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        registry.addInterceptor(this)
                .addPathPatterns("/**") // 拦截所有请求
                .excludePathPatterns(
                        "/doc.html",
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/v3/api-docs",
                        "/v3/api-docs/**",
                        "/swagger-resources/**",
                        "/webjars/**",
                        "/openapi.json",
                        "/.well-known/**",
                        "/error") // 排除无需校验的路径
        ;
    }

}
