package top.cloudlab.auth.application.service.impl;

import java.util.List;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import top.cloudlab.auth.application.dto.command.AbstractAccessTokenRequest;
import top.cloudlab.auth.application.dto.response.AccessTokenResponse;
import top.cloudlab.auth.application.service.TokenAppService;
import top.cloudlab.auth.common.exception.DomainException;

/**
 * 访问凭证主服务
 */
@Slf4j
@Primary
@Component
public class PrimaryAccessTokenService implements TokenAppService<AbstractAccessTokenRequest> {

    private final List<TokenAppService<AbstractAccessTokenRequest>> subServices;

    @SuppressWarnings("unchecked")
    public PrimaryAccessTokenService(List<? extends TokenAppService<? extends AbstractAccessTokenRequest>> subServices) {
        this.subServices = subServices.stream()
                .filter(service -> !(service instanceof PrimaryAccessTokenService))
                .map(s -> (TokenAppService<AbstractAccessTokenRequest>) s)
                .toList();
    }

    @Override
    public AccessTokenResponse generate(AbstractAccessTokenRequest command) {
        return subServices.stream()
                .map(service -> {
                    try {
                        return service.generate(command);
                    } catch (ClassCastException | UnsupportedOperationException e) {
                        log.error("服务 {} 无法处理请求: {}", service.getClass().getSimpleName(), e.getMessage());
                        return null;
                    }
                })
                .filter(result -> result != null)
                .findFirst()
                .orElseThrow(() -> DomainException.of("NO_ACCESS_SERVICE", "未找到合适的访问凭证服务"));
    }
}