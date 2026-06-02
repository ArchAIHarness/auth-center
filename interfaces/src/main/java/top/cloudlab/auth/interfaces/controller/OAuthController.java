package top.cloudlab.auth.interfaces.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;

import top.cloudlab.auth.interfaces.config.annotation.IgnoreToken;
import top.cloudlab.auth.interfaces.config.annotation.UserId;
import top.cloudlab.auth.application.dto.command.CreateOAuthTokenRequest;
import top.cloudlab.auth.application.dto.command.RefreshOAuthTokenRequest;
import top.cloudlab.auth.application.dto.response.AccessTokenResponse;
import top.cloudlab.auth.application.dto.response.AuthCodeResponse;
import top.cloudlab.auth.application.service.OAuthAppService;
import top.cloudlab.auth.application.service.impl.CreateOAuthTokenService;
import top.cloudlab.auth.application.service.impl.RefreshOAuthTokenService;
import top.cloudlab.auth.common.exception.DomainException;

import java.util.Map;

/**
 * 访问授权控制器
 * <p>
 * 提供 OAuth 2.0 授权码流程，支持第三方应用获取用户授权。
 *
 * @see OAuthAppService
 * @see CreateOAuthTokenService
 * @see RefreshOAuthTokenService
 */
@Slf4j
@Tag(name = "访问授权")
@RestController
@RequestMapping("/oauth")
@RequiredArgsConstructor
public class OAuthController {

    private final OAuthAppService oauthService;
    private final CreateOAuthTokenService createOAuthTokenService;
    private final RefreshOAuthTokenService refreshOAuthTokenService;

    /**
     * 获取用户授权码
     * <p>
     * 为已登录用户生成 OAuth 2.0 授权码，供第三方应用换取访问令牌。
     *
     * @param clientId 客户端ID
     * @param userId   当前登录用户ID（从上下文自动注入）
     * @return 授权码响应，包含授权码和过期时间
     */
    @Operation(summary = "获取用户授权码（client_id/client_screct，用于第三方获取用户授权码）")
    @PostMapping("/authorize")
    public AuthCodeResponse authorize(@Schema(description = "客户端id") @RequestParam(name = "client_id", required = true) String clientId,
            @UserId String userId) {
        log.info("clientId = {}", clientId);
        log.info("userId = {}", userId);
        return oauthService.authorize(clientId, userId);
    }

    /**
     * 获取或刷新用户授权令牌
     * <p>
     * 支持授权码模式和刷新令牌模式获取访问令牌。
     *
     * @param request 请求体，包含 grant_type、client_id、secret、code/refresh_token
     * @return 访问令牌响应，包含 access_token、refresh_token 和过期时间
     * @throws DomainException 不支持的授权类型时抛出
     */
    @IgnoreToken
    @Operation(summary = "获取/刷新用户授权令牌（client_id/client_screct，用于第三方获取/刷新用户授权令牌）")
    @PostMapping("/access_token")
    public AccessTokenResponse generate(@RequestBody Map<String, Object> request) {
        String grantType = getString(request, "grant_type", "grantType");
        String clientId = getString(request, "client_id", "clientId");
        String secret = getString(request, "secret");

        log.info("access_token - grantType: {}, clientId: {}, request: {}", grantType, clientId, request);

        if ("authorization_code".equals(grantType)) {
            CreateOAuthTokenRequest cmd = new CreateOAuthTokenRequest();
            cmd.setClientId(clientId);
            cmd.setSecret(secret);
            cmd.setCode(getString(request, "code"));
            return createOAuthTokenService.generate(cmd);
        } else if ("refresh_token".equals(grantType)) {
            RefreshOAuthTokenRequest cmd = new RefreshOAuthTokenRequest();
            cmd.setClientId(clientId);
            cmd.setSecret(secret);
            cmd.setRefreshToken(getString(request, "refresh_token", "refreshToken"));
            return refreshOAuthTokenService.generate(cmd);
        }
        throw DomainException.of("INVALID_GRANT_TYPE", "不支持的授权类型: " + grantType);
    }

    private String getString(Map<String, Object> map, String... keys) {
        for (String key : keys) {
            Object value = map.get(key);
            if (value != null) {
                return value.toString();
            }
        }
        return null;
    }

}