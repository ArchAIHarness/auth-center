package top.cloudlab.auth.application.service.impl;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import top.cloudlab.auth.application.dto.command.AbstractOwnerAccessTokenRequest;
import top.cloudlab.auth.application.dto.command.CreateAccessTokenRequest;
import top.cloudlab.auth.application.dto.response.AccessTokenResponse;
import top.cloudlab.auth.common.exception.DomainException;
import top.cloudlab.auth.domain.access.AccessToken;
import top.cloudlab.auth.domain.access.AccessTokenRepository;
import top.cloudlab.auth.domain.access.AuthType;
import top.cloudlab.auth.domain.access.Tokener;
import top.cloudlab.auth.domain.user.AccessSecret;
import top.cloudlab.auth.domain.user.UserDomainService;

@Slf4j
@Service
public class CreateAccessTokenService {

    private final UserDomainService userDomainService;
    private final Tokener tokener;
    private final AccessTokenRepository accessTokenRepository;

    public CreateAccessTokenService(final UserDomainService userDomainService,
            final Tokener tokener,
            final AccessTokenRepository accessTokenRepository) {
        this.userDomainService = userDomainService;
        this.tokener = tokener;
        this.accessTokenRepository = accessTokenRepository;
    }

    @Transactional
    public AccessTokenResponse generate(AbstractOwnerAccessTokenRequest command) {
        if (!(command instanceof CreateAccessTokenRequest)) {
            throw DomainException.of("INVALID_OPERATION", "无效的请求类型");
        }
        CreateAccessTokenRequest req = (CreateAccessTokenRequest) command;
        log.info("create access token with tenantId: {}", req.getTenantId());
        Optional<AccessSecret> optional = userDomainService.validate(req.getAk(), req.getSk());
        if (optional.isEmpty()) {
             throw DomainException.of("INVALID_OPERATION", "非法操作，无法执行");
        }
        AccessSecret secret = optional.get();

        Set<String> scopes;
        if (req.getTenantId() != null && !req.getTenantId().isBlank()) {
            scopes = Set.of(req.getTenantId());
        } else {
            scopes = secret.getScopes() != null ? secret.getScopes() : Collections.emptySet();
        }

        AccessToken token = AccessToken.create(tokener, secret.getUserId(), secret.getUserId(), AuthType.Auth, scopes, secret.getSk());
        accessTokenRepository.save(token);
        return AccessTokenResponse.convert(token);
    }

}