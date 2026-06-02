package top.cloudlab.auth.application.service.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import top.cloudlab.auth.application.dto.command.RefreshOAuthTokenRequest;
import top.cloudlab.auth.application.dto.response.AccessTokenResponse;
import top.cloudlab.auth.application.service.TokenAppService;
import top.cloudlab.auth.common.exception.DomainException;
import top.cloudlab.auth.domain.access.AccessToken;
import top.cloudlab.auth.domain.access.AccessTokenId;
import top.cloudlab.auth.domain.access.AccessTokenRepository;
import top.cloudlab.auth.domain.access.TokenClaims;
import top.cloudlab.auth.domain.access.Tokener;
import top.cloudlab.auth.domain.user.UserDomainService;

/**
 * 刷新授权访问凭证服务
 */
@Slf4j
@Service
public class RefreshOAuthTokenService implements TokenAppService<RefreshOAuthTokenRequest> {

    private final UserDomainService userDomainService;
    private final Tokener tokener;
    private final AccessTokenRepository accessTokenRepository;

    public RefreshOAuthTokenService(final UserDomainService userDomainService,
            final Tokener tokener,
            final AccessTokenRepository accessTokenRepository) {
        this.userDomainService = userDomainService;
        this.tokener = tokener;
        this.accessTokenRepository = accessTokenRepository;
    }

    @Override
    @Transactional
    public AccessTokenResponse generate(RefreshOAuthTokenRequest command) {
        log.info("refresh oauth token...");
        userDomainService.validate(command.getClientId(), command.getSecret());
        TokenClaims claims = tokener.parse(command.getRefreshToken());
        Optional<AccessToken> optional = accessTokenRepository.findById(AccessTokenId.of(claims.getId()));
        if (optional.isEmpty()) {
            throw DomainException.of("INVALID_TOKEN", "无效token");
        }
        AccessToken token = optional.get();
        if (!token.isOAuthToken()) {
             throw DomainException.of("INVALID_OPERATION", "非法操作，无法执行");
        }
        AccessToken newToken = token.refresh();
        accessTokenRepository.save(token);
        accessTokenRepository.save(newToken);
        return AccessTokenResponse.convert(newToken);
    }

}
