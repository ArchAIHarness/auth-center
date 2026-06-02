package top.cloudlab.auth.interfaces.config.resolver;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import top.cloudlab.auth.common.context.UserContext;
import top.cloudlab.auth.interfaces.config.annotation.UserId;

/**
 * UserId 参数解析器
 * 从 UserContext 获取当前用户 ID
 */
@Configuration
public class UserIdRequestResolver implements HandlerMethodArgumentResolver, WebMvcConfigurer {

    @Override
    public boolean supportsParameter(@NonNull MethodParameter parameter) {
        // 只处理带有 @UserId 注解的参数
        return parameter.hasParameterAnnotation(UserId.class);
    }

    @Override
    public Object resolveArgument(@NonNull MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer,
            @NonNull NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) throws Exception {
        if (parameter.hasParameterAnnotation(UserId.class)) {
            // 从 UserContext 获取当前用户 ID
            return UserContext.getUserId();
        }
        return null;
    }

    @Override
    public void addArgumentResolvers(@NonNull List<HandlerMethodArgumentResolver> resolvers) {
        // 注册自定义的 UserId 参数解析器
        resolvers.add(this);
    }

}
