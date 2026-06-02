package top.cloudlab.auth.interfaces.config.async;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.TaskDecorator;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import lombok.extern.slf4j.Slf4j;
import top.cloudlab.auth.common.context.RequestContext;
import top.cloudlab.auth.common.context.UserContext;

import java.util.Map;
import java.util.concurrent.Executor;

/**
 * 异步执行器
 * 支持上下文传播：RequestContext、UserContext、MDC
 */
@Slf4j
@Configuration
@EnableAsync
public class AsyncExecutor implements TaskDecorator {

    @Primary
    @Bean
    public Executor asyncTaskExecutor() {
        int cpuCoreNum = Runtime.getRuntime().availableProcessors();
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 线程池基础配置（按需调整）
        executor.setCorePoolSize(cpuCoreNum + 1);
        executor.setMaxPoolSize(cpuCoreNum * 2 + 1);
        executor.setQueueCapacity((cpuCoreNum + 1) * 5);
        executor.setThreadNamePrefix("Async-Thread-");
        // 关键：设置上下文装饰器，自动传递上下文
        executor.setTaskDecorator(this);
        executor.initialize();
        return executor;
    }

    @NonNull
    @Override
    public Runnable decorate(@NonNull Runnable runnable) {
        // 1. 获取父线程上下文
        Map<String, String> requestContextMap = RequestContext.getAll();
        String userId = UserContext.getUserId();
        String tenantId = UserContext.getTenantId();
        
        // 2. 子线程：执行任务前设置上下文，执行后清理（避免线程池复用污染）
        return () -> {
            try {
                // 设置 RequestContext
                if (!requestContextMap.isEmpty()) {
                    RequestContext.setAll(requestContextMap);
                }
                // 设置 UserContext
                if (userId != null) {
                    UserContext.setUserId(userId);
                }
                if (tenantId != null) {
                    UserContext.setTenantId(tenantId);
                }
                
                runnable.run(); // 执行异步业务逻辑
            } finally {
                // 强制清理，防止线程池复用
                RequestContext.clear();
                UserContext.clear();
            }
        };
    }
}
