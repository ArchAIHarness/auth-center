package top.cloudlab.auth.domain.access;

public interface Tokener {

    /**
     * 生成令牌
     * @param claims 令牌声明
     * @param secret 签名密钥
     * @return 令牌
     */
    String generate(TokenClaims claims, String secret);

    /**
     * 解析令牌
     * @param token 令牌
     * @return 令牌声明
     */
    TokenClaims parse(String token);

    /**
     * 验证令牌
     * @param token 令牌
     * @param secret 签名密钥
     * @return 令牌声明
     */
    TokenClaims validate(String token, String secret);

}
