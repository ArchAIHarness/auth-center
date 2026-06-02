package top.cloudlab.auth.interfaces.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

import top.cloudlab.auth.application.dto.command.RefreshAccessTokenRequest;
import top.cloudlab.auth.domain.user.TenantPermissions;
import top.cloudlab.auth.domain.user.UserDomainService;
import top.cloudlab.auth.interfaces.config.annotation.IgnoreToken;
import top.cloudlab.auth.interfaces.config.annotation.UserId;
import top.cloudlab.auth.application.dto.command.AbstractOwnerAccessTokenRequest;
import top.cloudlab.auth.application.dto.response.AccessTokenResponse;
import top.cloudlab.auth.application.dto.response.AccessValidatedResponse;
import top.cloudlab.auth.application.dto.response.UserInfoResponse;
import top.cloudlab.auth.application.service.AuthAppService;
import top.cloudlab.auth.application.service.impl.RefreshAccessTokenService;
import top.cloudlab.auth.application.service.impl.CreateAccessTokenService;

/**
 * 访问认证控制器
 * <p>
 * 提供访问令牌的验证、创建、刷新和用户信息查询功能。
 *
 * @see AuthAppService
 * @see CreateAccessTokenService
 * @see RefreshAccessTokenService
 */
@Slf4j
@Tag(name = "访问认证")
@IgnoreToken
@RestController
@RequestMapping
@RequiredArgsConstructor
public class AuthController {

    private final AuthAppService authService;
    private final CreateAccessTokenService createTokenService;
    private final RefreshAccessTokenService refreshTokenService;
    private final UserDomainService userDomainService;

    /**
     * 验证访问令牌
     *
     * @param token    Authorization 头中的 Bearer 令牌
     * @param response HTTP 响应，用于设置用户上下文头
     * @return 验证结果，包含用户ID、租户ID和授权范围
     */
    @Operation(summary = "访问认证")
    @GetMapping
    public AccessValidatedResponse auth(@RequestHeader(value = "Authorization", required = false) String token,
            HttpServletResponse response) {
        AccessValidatedResponse res = authService.verify(token);
        if (!res.getIsValid()) {
            return res;
        }
        log.info("访问凭证详情: {}", res.getAccess());
        String userId = res.getAccess().getUserId();

        TenantPermissions tenantPermissions = userDomainService.getTenantPermissions(userId);
        Map<String, List<String>> permissions = tenantPermissions != null ? tenantPermissions.getPermissions() : null;
        Set<String> tenantIds = permissions != null ? permissions.keySet() : java.util.Set.of();

        response.setHeader("x-user-id", userId);
        response.setHeader("x-tenant-ids", String.join(",", tenantIds));
        if (tenantIds.size() == 1) {
            response.setHeader("x-tenant-id", tenantIds.iterator().next());
        }
        return res;
    }

    /**
     * 创建访问凭证
     *
     * @param command 创建令牌请求，包含 AK/SK 和可选的租户ID
     * @return 访问令牌响应，包含 access_token 和 refresh_token
     */
    @Operation(summary = "创建访问凭证")
    @PostMapping("/token")
    public AccessTokenResponse createAccessToken(@Valid @RequestBody AbstractOwnerAccessTokenRequest command) {
        log.info("创建访问凭证: {}", command);
        return createTokenService.generate(command);
    }

    /**
     * 刷新访问凭证
     *
     * @param refreshToken Authorization 头中的刷新令牌
     * @return 新的访问令牌响应
     */
    @Operation(summary = "刷新访问凭证")
    @PostMapping("/refresh_token")
    public AccessTokenResponse refreshAccessToken(
            @Schema(description = "刷新令牌") @RequestHeader("Authorization") String refreshToken) {
        RefreshAccessTokenRequest command = RefreshAccessTokenRequest.builder()
                .refreshToken(refreshToken)
                .build();
        return refreshTokenService.generate(command);
    }

    /**
     * 获取用户身份信息
     *
     * @param userId 当前登录用户ID（从上下文自动注入）
     * @return 用户信息响应
     */
    @Operation(summary = "获取用户身份信息")
    @GetMapping("/userinfo")
    public UserInfoResponse userinfo(@UserId String userId) {
        log.info("获取用户身份信息: {}", userId);
        return authService.userinfo(userId);
    }

}