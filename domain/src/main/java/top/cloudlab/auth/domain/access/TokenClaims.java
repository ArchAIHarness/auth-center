package top.cloudlab.auth.domain.access;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;

import lombok.Builder;
import lombok.Getter;

/**
 * 令牌声明
 */
@Builder
@Getter
public class TokenClaims {

    /**
     * 访问令牌ID
     */
    private final String id;

    /**
     * 用户ID
     */
    private final String subject;

    /**
     * 签发者
     */
    private final String issuer;

    /**
     * 创建时间
     */
    private final LocalDateTime createTime;

    /**
     * 有效期，单位：秒
     */
    private final Long expireInSeconds;

    /**
     * 内容负载
     */
    private final Map<String, Object> payload;

    public static TokenClaims of(String id, String subject, String issuer, LocalDateTime createTime, Long expireInSeconds, Map<String, Object> payload) {
        return new TokenClaims(id, subject, issuer, createTime, expireInSeconds, payload);
    }

    protected TokenClaims(String id, String subject, String issuer, LocalDateTime createTime, Long expireInSeconds, Map<String, Object> payload) {
        this.id = id;
        this.subject = subject;
        this.issuer = issuer;
        this.createTime = createTime;
        this.expireInSeconds = expireInSeconds;
        this.payload = payload;
    }

    /**
     * 签发时间
     */
    public Instant getIssuedAt() {
        if (createTime == null) {
            return null;
        }
        return createTime.atZone(ZoneId.systemDefault()).toInstant();
    }

    /**
     * 过期时间
     */
    public Instant getExpiresAt() {
        Instant instant = getIssuedAt();
        if (instant == null || expireInSeconds == null) {
            return null;
        }
        return instant.plusSeconds(expireInSeconds);
    }

}
