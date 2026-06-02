package top.cloudlab.auth.domain.oauth;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import top.cloudlab.auth.common.base.AggregateRoot;

/**
 * OAuth 2.0 授权码聚合根
 * <p>
 * 临时凭证，用于 OAuth 2.0 授权码流程中第三方应用获取用户授权。
 * 授权码一次性使用，验证后标记为已使用。
 *
 * @see CodeGenerator
 * @see AuthCodeRepository
 */
@Builder
@Getter
public class AuthCode extends AggregateRoot {

    /**
     * 授权码
     */
    private final String code;

    /**
     * 用户ID
     */
    private final String userId;

    /**
     * 客户端ID
     */
    private final String clientId;

    /**
     * 过期时间（秒）
     */
    private final Long expireInSeconds;

    /**
     * 创建时间
     */
    private final LocalDateTime createTime;

    /**
     * 状态
     */
    private CodeStatus status;

    /**
     * 创建授权码
     *
     * @param generator 授权码生成器
     * @param userId    用户ID
     * @param clientId  客户端ID
     * @return 新的授权码实例，默认有效期 600 秒
     */
    public static AuthCode create(CodeGenerator generator, String userId, String clientId) {
        if (generator == null) {
            throw new IllegalArgumentException("generator is required");
        }
        if (clientId == null) {
            throw new IllegalArgumentException("clientId is required");
        }
        String code = generator.generate(clientId, userId);
        return new AuthCode(code, userId, clientId, 600L, LocalDateTime.now(), CodeStatus.VALID);
    }

    /**
     * 从持久化数据重建授权码
     *
     * @param code            授权码
     * @param userId          用户ID
     * @param clientId        客户端ID
     * @param expireInSeconds 过期时间（秒）
     * @param createTime      创建时间
     * @param status          授权码状态
     * @return 重建的授权码实例，若已过期则状态为 EXPIRED
     */
    public static AuthCode reconstruct(String code, String userId, String clientId, Long expireInSeconds, LocalDateTime createTime, CodeStatus status) {
        CodeStatus finalStatus = status;
        if (CodeStatus.VALID.equals(status)
                && LocalDateTime.now().isAfter(createTime.plusSeconds(expireInSeconds))) {
            finalStatus = CodeStatus.EXPIRED;
        }
        return new AuthCode(code, userId, clientId, expireInSeconds, createTime, finalStatus);
    }

    protected AuthCode(String code, String userId, String clientId, Long expireInSeconds, LocalDateTime createTime, CodeStatus status) {
        this.code = code;
        this.userId = userId;
        this.clientId = clientId;
        this.expireInSeconds = expireInSeconds;
        this.createTime = createTime;
        this.status = status;
    }

    /**
     * 验证授权码
     * <p>
     * 检查客户端ID是否匹配、授权码是否有效且未过期。
     * 验证成功后标记授权码为已使用（一次性）。
     *
     * @param clientId 待验证的客户端ID
     * @return 验证通过返回 true，否则返回 false
     */
    public boolean validate(String clientId) {
        if (!this.clientId.equals(clientId)) {
            return false;
        }
        if (!CodeStatus.VALID.equals(this.status)) {
            return false;
        }
        if (this.createTime != null && LocalDateTime.now().isAfter(this.createTime.plusSeconds(this.expireInSeconds))) {
            this.status = CodeStatus.EXPIRED;
            return false;
        }
        this.status = CodeStatus.USED;
        return true;
    }

}
