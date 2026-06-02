package top.cloudlab.auth.domain.user;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Set;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import lombok.Builder;
import lombok.Getter;

/**
 * 访问密钥值对象
 * <p>
 * 表示用户的 AK/SK 密钥对，用于 API 访问认证和签名验证。
 *
 * @see UserDomainService
 */
@Builder
@Getter
public class AccessSecret {
    private final String userId;
    private final String ak;
    private final String sk;
    private final Set<String> scopes;

    /**
     * 创建访问密钥实例
     *
     * @param userId 用户ID
     * @param ak     访问密钥ID
     * @param sk     访问密钥Secret
     * @param scopes 授权范围
     * @return 新的访问密钥实例
     */
    public static AccessSecret of(String userId, String ak, String sk, Set<String> scopes) {
        return new AccessSecret(userId, ak, sk, scopes);
    }

    protected AccessSecret(String userId, String ak, String sk, Set<String> scopes) {
        this.userId = userId;
        this.ak = ak;
        this.sk = sk;
        this.scopes = scopes;
    }

    /**
     * 验证签名
     *
     * @param toSignString 待签名的字符串
     * @param sign         待验证的签名
     * @return 签名匹配返回 true
     */
    public boolean verify(String toSignString, String sign) {
        return sign(toSignString).equals(sign);
    }

    /**
     * 使用 HMAC-SHA256 生成签名
     *
     * @param toSignString 待签名的字符串
     * @return Base64 编码的签名字符串
     */
    public String sign(String toSignString) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(sk.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] hash = mac.doFinal(toSignString.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Failed to sign", e);
        }
    }
}
