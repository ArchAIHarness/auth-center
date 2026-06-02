package top.cloudlab.auth.infrastructure.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import top.cloudlab.auth.common.annotation.Cache;

/**
 * 缓存注解 AOP 拦截器
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
@SuppressWarnings("null")
public class CacheAspect {

    private final RedisTemplate<String, Object> redisTemplate;

    @Around("@annotation(cache)")
    public Object around(ProceedingJoinPoint joinPoint, Cache cache) throws Throwable {
        if (!cache.enabled()) {
            return joinPoint.proceed();
        }

        String key = buildKey(joinPoint, cache);
        Object cached = redisTemplate.opsForValue().get(key);
        if (cached != null) {
            log.debug("Cache hit for key: {}", key);
            return cached;
        }

        Object result = joinPoint.proceed();
        if (result != null) {
            redisTemplate.opsForValue().set(key, result, cache.expire(), TimeUnit.SECONDS);
            log.debug("Cache set for key: {}, expire: {}s", key, cache.expire());
        }
        return result;
    }

    private String buildKey(ProceedingJoinPoint joinPoint, Cache cache) {
        String prefix = cache.value();
        String args = Arrays.stream(joinPoint.getArgs())
                .map(arg -> arg != null ? arg.toString() : "null")
                .collect(Collectors.joining(":"));
        return prefix + ":" + joinPoint.getSignature().toShortString() + ":" + args;
    }
}
