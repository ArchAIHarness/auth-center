package top.cloudlab.auth.interfaces.config.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import top.cloudlab.auth.common.context.RequestContext;
import top.cloudlab.auth.common.context.UserContext;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.stream.StreamSupport;

/**
 * 应用上下文过滤器
 * 负责从 HTTP 请求中提取请求头存入 RequestContext，并处理 Bearer Token
 */
@Slf4j
@Component
@WebFilter(urlPatterns = "/*")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AppContextFilter implements Filter {

    // 定义常用请求头常量
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String TRACE_ID_HEADER = "x-trace-id";
    private static final String USER_ID_HEADER = "x-user-id";
    private static final String TENANT_ID_HEADER = "x-tenant-id";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        
        try {
            // 1. 提取所有请求头存入 RequestContext
            Enumeration<String> headerNames = httpRequest.getHeaderNames();
            StreamSupport.stream(Collections.list(headerNames).spliterator(), false)
                    .forEach(headerName -> RequestContext.set(headerName, httpRequest.getHeader(headerName)));
            
            // 2. 设置 traceId 到 MDC
            String traceId = httpRequest.getHeader(TRACE_ID_HEADER);
            if (traceId != null) {
                MDC.put("traceId", traceId);
            }
            
            // 3. 设置用户上下文
            String userId = httpRequest.getHeader(USER_ID_HEADER);
            String tenantId = httpRequest.getHeader(TENANT_ID_HEADER);
            if (userId != null) {
                UserContext.setUserId(userId);
            }
            if (tenantId != null) {
                UserContext.setTenantId(tenantId);
            }
            
            log.info("应用上下文初始化完成，请求URI:{}", httpRequest.getRequestURI());
            
            // 4. 包装请求，自动去除 Bearer 前缀
            HttpServletRequest wrappedRequest = new BearerTokenRequestWrapper(httpRequest);
            
            // 5. 继续执行过滤器链
            chain.doFilter(wrappedRequest, response);
            
        } finally {
            // 6. 清理上下文，防止内存泄漏
            RequestContext.clear();
            UserContext.clear();
            MDC.remove("traceId");
            log.info("应用上下文已清空，请求URI:{}", httpRequest.getRequestURI());
        }
    }

    /**
     * 请求包装器：重写 Authorization 头的获取逻辑，自动去除 Bearer 前缀
     */
    private static class BearerTokenRequestWrapper extends HttpServletRequestWrapper {
        
        private final String pureToken;

        public BearerTokenRequestWrapper(HttpServletRequest request) {
            super(request);
            // 处理 Authorization 头
            String authHeader = request.getHeader(AUTHORIZATION_HEADER);
            if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
                // 去除 Bearer 前缀，保留纯 Token
                this.pureToken = authHeader.substring(BEARER_PREFIX.length()).trim();
            } else {
                this.pureToken = authHeader;
            }
        }

        /**
         * 重写 getHeader 方法，返回处理后的 Authorization 头
         */
        @Override
        public String getHeader(String name) {
            if (AUTHORIZATION_HEADER.equalsIgnoreCase(name)) {
                return pureToken;
            }
            return super.getHeader(name);
        }

        /**
         * 重写 getHeaders 方法（兼容多值场景）
         */
        @Override
        public Enumeration<String> getHeaders(String name) {
            if (AUTHORIZATION_HEADER.equalsIgnoreCase(name) && pureToken != null) {
                return Collections.enumeration(Collections.singletonList(pureToken));
            }
            return super.getHeaders(name);
        }
    }
}
