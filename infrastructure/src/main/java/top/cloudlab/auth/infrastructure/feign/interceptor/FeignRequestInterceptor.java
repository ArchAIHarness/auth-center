package top.cloudlab.auth.infrastructure.feign.interceptor;

import java.util.Enumeration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@Primary
public class FeignRequestInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            log.debug("当前无有效请求上下文，跳过请求头透传");
            return;
        }

        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames == null) {
            return;
        }
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            if (!headerName.toLowerCase().startsWith("x-")) {
                continue;
            }
            String headerValue = request.getHeader(headerName);
            if (headerValue == null || headerValue.isBlank()) {
                log.debug("跳过空值请求头：{}", headerName);
                continue;
            }
            if (headerValue.contains("{") || headerValue.contains("}")) {
                log.debug("跳过包含特殊字符的请求头：{}", headerName);
                continue;
            }
            log.debug("透传请求头：{} = {}", headerName, headerValue);
            template.header(headerName, headerValue);
        }
    }
}