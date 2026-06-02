package top.cloudlab.auth.application.service.impl;

import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;import top.cloudlab.auth.application.dto.response.AccessDetailResponse;
import top.cloudlab.auth.application.dto.response.AccessValidatedResponse;
import top.cloudlab.auth.application.dto.response.UserInfoResponse;
import top.cloudlab.auth.application.service.AuthAppService;
import top.cloudlab.auth.common.exception.DomainException;
import top.cloudlab.auth.domain.access.AccessToken;
import top.cloudlab.auth.domain.access.AccessTokenId;
import top.cloudlab.auth.domain.access.AccessTokenRepository;
import top.cloudlab.auth.domain.access.AuthType;
import top.cloudlab.auth.domain.access.TokenClaims;
import top.cloudlab.auth.domain.access.Tokener;
import top.cloudlab.auth.domain.user.UserDomainService;
import top.cloudlab.auth.domain.user.UserInfo;

/**
 * 认证应用服务实现
 * <p>
 * 提供令牌验证和用户信息查询功能。
 *
 * @see AuthAppService
 */
@Slf4j
@Service
public class AuthAppServiceImpl implements AuthAppService {

    private final Tokener tokener;
    private final AccessTokenRepository accessTokenRepository;
    private final UserDomainService userDomainService;
    private final String secret;

    public AuthAppServiceImpl(Tokener tokener, AccessTokenRepository accessTokenRepository,
            UserDomainService userDomainService, @Value("${auth.secret}") String secret) {
        this.tokener = tokener;
        this.accessTokenRepository = accessTokenRepository;
        this.userDomainService = userDomainService;
        this.secret = secret;
    }

    /**
     * 验证访问令牌
     *
     * @param token 访问令牌字符串（Bearer token）
     * @return 验证结果，包含令牌是否有效及访问详情
     */
    @Override
    public AccessValidatedResponse verify(String token) {
        boolean isValid = true;
        AccessDetailResponse access = null;
        try {
            TokenClaims claims = tokener.parse(token);
            Optional<AccessToken> optional = accessTokenRepository.findById(AccessTokenId.of(claims.getId()));
            if (optional.isEmpty()) {
                claims = tokener.validate(token, secret);
                optional = Optional.of(AccessToken.create(tokener, claims.getSubject(), claims.getIssuer(), AuthType.Auth, Set.of(), secret));
            }
            AccessToken accessToken = optional.get();
            accessToken.verify(token);
            access = AccessDetailResponse.builder()
                    .userId(accessToken.getUserId())
                    .scopes(Set.copyOf(accessToken.getScopes()))
                    .accessToken(accessToken.getAccessToken())
                    .refreshToken(accessToken.getRefreshToken())
                    .build();
        } catch (Exception e) {
            log.warn("Token验证失败", e);
            isValid = false;
        }
        AccessValidatedResponse.AccessValidatedResponseBuilder builder = AccessValidatedResponse.builder()
                .isValid(isValid);
        if (access != null) {
            builder.access(access);
        }
        return builder.build();
    }

    /**
     * 获取用户身份信息
     *
     * @param userId 用户ID
     * @return 用户信息响应
     * @throws DomainException 用户不存在时抛出异常
     */
    @Override
    public UserInfoResponse userinfo(String userId) {
        UserInfo info = userDomainService.getUserInfo(userId)
                .orElseThrow(() -> DomainException.of("INVALID_OPERATION", "非法操作，无法执行"));
        return UserInfoResponse.convert(info);
    }

}
