package top.cloudlab.auth.application.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import top.cloudlab.auth.application.dto.command.RefreshAccessTokenRequest;
import top.cloudlab.auth.application.dto.response.AccessTokenResponse;
import top.cloudlab.auth.common.exception.DomainException;
import top.cloudlab.auth.domain.access.AccessToken;
import top.cloudlab.auth.domain.access.AccessTokenId;
import top.cloudlab.auth.domain.access.AccessTokenRepository;
import top.cloudlab.auth.domain.access.AuthType;
import top.cloudlab.auth.domain.access.TokenClaims;
import top.cloudlab.auth.domain.access.Tokener;

@Slf4j
@Service
public class RefreshAccessTokenService {

    private final Tokener tokener;
    private final AccessTokenRepository accessTokenRepository;

    public RefreshAccessTokenService(final Tokener tokener,
            final AccessTokenRepository accessTokenRepository) {
        this.tokener = tokener;
        this.accessTokenRepository = accessTokenRepository;
    }

    @Transactional
    public AccessTokenResponse generate(RefreshAccessTokenRequest command) {
        log.info("refresh access token");
        
        String authorization = command.getRefreshToken();
        if (authorization == null || authorization.isBlank()) {
            throw DomainException.of("INVALID_OPERATION", "Authorization header 不能为空");
        }
        
        if (authorization.startsWith("Bearer ")) {
            authorization = authorization.substring(7);
        }
        
        TokenClaims claims = tokener.parse(authorization);
        String userId = claims.getSubject();
        if (userId == null) {
            throw DomainException.of("INVALID_OPERATION", "无效的令牌");
        }
        
        Optional<AccessToken> existingToken = accessTokenRepository.findById(AccessTokenId.of(claims.getId()));
        if (existingToken.isEmpty()) {
            throw DomainException.of("INVALID_OPERATION", "令牌记录不存在");
        }
        String secret = existingToken.get().getSecret();
        
        @SuppressWarnings("unchecked")
        Set<String> scopes = claims.getPayload().get("scopes") instanceof List 
            ? new java.util.HashSet<>((java.util.List<String>) claims.getPayload().get("scopes"))
            : (Set<String>) claims.getPayload().get("scopes");
        
        AccessToken token = AccessToken.create(tokener, userId, userId, AuthType.Auth, scopes, secret);
        accessTokenRepository.save(token);
        return AccessTokenResponse.convert(token);
    }

}