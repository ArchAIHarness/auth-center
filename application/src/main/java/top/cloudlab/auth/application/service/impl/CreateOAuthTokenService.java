package top.cloudlab.auth.application.service.impl;


import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import top.cloudlab.auth.application.dto.command.CreateOAuthTokenRequest;
import top.cloudlab.auth.application.dto.response.AccessTokenResponse;
import top.cloudlab.auth.application.service.TokenAppService;
import top.cloudlab.auth.common.exception.DomainException;
import top.cloudlab.auth.domain.access.AccessToken;
import top.cloudlab.auth.domain.access.AccessTokenRepository;
import top.cloudlab.auth.domain.access.AuthType;
import top.cloudlab.auth.domain.access.Tokener;
import top.cloudlab.auth.domain.oauth.AuthCode;
import top.cloudlab.auth.domain.oauth.AuthCodeRepository;
import top.cloudlab.auth.domain.user.AccessSecret;
import top.cloudlab.auth.domain.user.UserDomainService;

@Slf4j
@Service
public class CreateOAuthTokenService implements TokenAppService<CreateOAuthTokenRequest> {

    private final UserDomainService userDomainService;
    private final Tokener tokener;
    private final AccessTokenRepository accessTokenRepository;
    private final AuthCodeRepository authCodeRepository;

    public CreateOAuthTokenService(final UserDomainService userDomainService,
            final Tokener tokener,
            final AccessTokenRepository accessTokenRepository,
            final AuthCodeRepository authCodeRepository) {
        this.userDomainService = userDomainService;
        this.tokener = tokener;
        this.accessTokenRepository = accessTokenRepository;
        this.authCodeRepository = authCodeRepository;
    }

    @Override
    @Transactional
    public AccessTokenResponse generate(CreateOAuthTokenRequest command) {
        log.info("create oauth token...");
        Optional<AccessSecret> optional = userDomainService.validate(command.getClientId(), command.getSecret());
        if (optional.isEmpty()) {
            throw DomainException.of("INVALID_CREDENTIALS", "client_id or client_secret is invalid");
        }
        AccessSecret secret = optional.get();
        AuthCode code = authCodeRepository.findFirst(command.getClientId(), command.getCode())
                .orElseThrow(() -> DomainException.of("INVALID_OPERATION", "非法操作，无法执行"));
        if (!code.validate(command.getClientId())) {
             throw DomainException.of("INVALID_OPERATION", "非法操作，无法执行");
        }
        authCodeRepository.save(code);
        AccessToken token = AccessToken.create(tokener, code.getUserId(), secret.getUserId(), AuthType.OAuth, Set.of(), secret.getSk());
        accessTokenRepository.save(token);
        return AccessTokenResponse.convert(token);
    }

}
