package top.cloudlab.auth.domain.access;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.Builder;
import lombok.Getter;
import top.cloudlab.auth.common.base.AggregateRoot;

/**
 * 访问令牌聚合根
 * <p>
 * 表示用户身份验证后生成的访问凭证，包含访问令牌和刷新令牌。
 *
 * @see Tokener
 * @see AccessTokenRepository
 */
@Builder
@Getter
public class AccessToken extends AggregateRoot {

    /**
     * 访问令牌ID
     */
    private final AccessTokenId id;

    /**
     * 令牌器
     */
    private final Tokener tokener;

    /**
     * 令牌用户ID
     */
    private final String userId;

    /**
     * 持有用户ID
     */
    private final String issuerId;

    /**
     * 令牌类型
     */
    private final TokenType tokenType;

    /**
     * 认证类型，当类型为授权时 issuerId 是三方，否则是用户
     */
    private final AuthType authType;

    /**
     * 授权范围
     */
    private final Set<String> scopes;

    /**
     * 签名密钥
     */
    private final String secret;

    /**
     * 创建时间
     */
    private final LocalDateTime createTime;

    /**
     * 过期时间，单位秒
     */
    private final Long expireInSeconds;

    /**
     * 刷新令牌过期时间，单位秒
     */
    private final Long refreshExpireInSeconds;

    /**
     * 令牌状态
     */
    private TokenStatus status;

    /**
     * 创建访问令牌
     *
     * @param tokener  令牌生成器
     * @param userId   用户ID
     * @param issuerId 签发者ID
     * @param authType 认证类型
     * @param scopes   授权范围
     * @param secret   签名密钥
     * @return 新的访问令牌实例
     */
    public static AccessToken create(Tokener tokener, String userId, String issuerId, AuthType authType, Set<String> scopes, String secret) {
        return new AccessToken(
            AccessTokenId.generate(),
            tokener,
            userId,
            issuerId,
            TokenType.Bearer,
            authType,
            scopes,
            secret,
            LocalDateTime.now(),
            7200L,
            604800L,
            TokenStatus.VALID
        );
    }

    /**
     * 从持久化数据重建访问令牌
     *
     * @param id                   令牌ID
     * @param tokener              令牌生成器
     * @param userId               用户ID
     * @param issuerId             签发者ID
     * @param tokenType            令牌类型
     * @param authType             认证类型
     * @param scopes               授权范围
     * @param secret               签名密钥
     * @param createTime           创建时间
     * @param expireInSeconds      访问令牌过期时间（秒）
     * @param refreshExpireInSeconds 刷新令牌过期时间（秒）
     * @param status               令牌状态
     * @return 重建的访问令牌实例
     */
    public static AccessToken reconstruct(AccessTokenId id, Tokener tokener, String userId, String issuerId, TokenType tokenType, AuthType authType, Set<String> scopes, String secret, LocalDateTime createTime, Long expireInSeconds, Long refreshExpireInSeconds, TokenStatus status) {
        return new AccessToken(
            id,
            tokener,
            userId,
            issuerId,
            tokenType,
            authType,
            scopes,
            secret,
            createTime,
            expireInSeconds,
            refreshExpireInSeconds,
            status
        );
    }

    protected AccessToken(AccessTokenId id, Tokener tokener, String userId, String issuerId, TokenType tokenType, AuthType authType, Set<String> scopes, String secret, LocalDateTime createTime, Long expireInSeconds, Long refreshExpireInSeconds, TokenStatus status) {
        this.id = id;
        this.tokener = tokener;
        this.userId = userId;
        this.issuerId = issuerId;
        this.tokenType = tokenType;
        this.authType = authType;
        this.scopes = scopes;
        this.secret = secret;
        this.createTime = createTime;
        this.expireInSeconds = expireInSeconds;
        this.refreshExpireInSeconds = refreshExpireInSeconds;
        this.status = status;
    }

    /**
     * 获取访问令牌的声明信息
     *
     * @return 包含令牌ID、用户ID、过期时间等信息的声明对象
     */
    public TokenClaims getAccessTokenClaims() {
        Set<String> scopesCopy = this.scopes != null ? this.scopes : Set.of();
        return TokenClaims.of(
            this.id.value(),
            this.userId,
            this.issuerId,
            this.createTime,
            this.expireInSeconds,
            Map.of("scopes", List.copyOf(scopesCopy))
        );
    }

    /**
     * 获取刷新令牌的声明信息
     *
     * @return 包含令牌ID、用户ID、刷新过期时间等信息的声明对象
     */
    public TokenClaims getRefreshTokenClaims() {
        Set<String> scopesCopy = this.scopes != null ? this.scopes : Set.of();
        return TokenClaims.of(
            this.id.value(),
            this.userId,
            this.issuerId,
            this.createTime,
            this.refreshExpireInSeconds,
            Map.of("scopes", List.copyOf(scopesCopy))
        );
    }

    /**
     * 获取生成的访问令牌字符串
     *
     * @return JWT 格式的访问令牌
     */
    public String getAccessToken() {
        return this.tokener.generate(this.getAccessTokenClaims(), this.secret);
    }

    /**
     * 判断是否为认证令牌
     *
     * @return 如果是用户认证令牌返回 true
     */
    public boolean isAuthToken() {
        return AuthType.Auth.equals(this.authType);
    }

    /**
     * 判断是否为 OAuth 授权令牌
     *
     * @return 如果是 OAuth 授权令牌返回 true
     */
    public boolean isOAuthToken() {
        return AuthType.OAuth.equals(this.authType);
    }

    /**
     * 获取生成的刷新令牌字符串
     *
     * @return JWT 格式的刷新令牌
     */
    public String getRefreshToken() {
        return this.tokener.generate(this.getRefreshTokenClaims(), this.secret);
    }

    /**
     * 验证令牌有效性
     *
     * @param token 待验证的令牌字符串
     * @throws RuntimeException 验证失败时抛出异常
     */
    public void verify(String token) {
        this.tokener.validate(token, this.secret);
        this.validate();
    }

    /**
     * 验证并更新令牌状态
     * <p>
     * 检查令牌是否过期，更新状态为 EXPIRED 或 INVALID
     */
    public void validate() {
        if (TokenStatus.REVOKED.equals(this.status)) {
            return;
        }
        if (this.getAccessTokenClaims().getExpiresAt() != null && LocalDateTime.now()
                .isAfter(this.getAccessTokenClaims().getExpiresAt().atZone(ZoneId.systemDefault()).toLocalDateTime())) {
            this.status = TokenStatus.EXPIRED;
            return;
        }
        if (this.getRefreshTokenClaims().getExpiresAt() != null && LocalDateTime.now()
                .isAfter(this.getRefreshTokenClaims().getExpiresAt().atZone(ZoneId.systemDefault()).toLocalDateTime())) {
            this.status = TokenStatus.REVOKED;
            return;
        }
    }

    /**
     * 判断访问令牌是否已过期
     *
     * @return 如果已过期返回 true
     */
    public boolean isExpired() {
        validate();
        return TokenStatus.EXPIRED.equals(this.status);
    }

    /**
     * 判断令牌是否无效
     *
     * @return 如果刷新令牌过期或已吊销返回 true
     */
    public boolean isInvalid() {
        validate();
        return TokenStatus.REVOKED.equals(this.status);
    }

    /**
     * 吊销当前令牌
     * <p>
     * 将令牌状态设置为 INVALID，使令牌失效
     */
    public void revoke() {
        validate();
        // 否则直接设置为无效
        this.status = TokenStatus.REVOKED;
    }

    /**
     * 刷新令牌
     * <p>
     * 吊销当前令牌并生成新的访问令牌和刷新令牌
     *
     * @return 新的访问令牌实例
     * @throws IllegalStateException 令牌已过期或无效时抛出
     */
    public AccessToken refresh() {
        // 首先校验当前令牌状态
        validate();
        // 如果已经无效直接返回
        if (TokenStatus.REVOKED.equals(this.status)) {
            throw new IllegalStateException("令牌已过期或无效令牌，无法刷新");
        }
        revoke();
        return reconstruct(
                AccessTokenId.generate(),
                this.tokener,
                this.userId,
                this.issuerId,
                this.tokenType,
                this.authType,
                this.scopes,
                this.secret,
                LocalDateTime.now(),
                this.expireInSeconds,
                this.refreshExpireInSeconds,
                TokenStatus.VALID
        );
    }

}
