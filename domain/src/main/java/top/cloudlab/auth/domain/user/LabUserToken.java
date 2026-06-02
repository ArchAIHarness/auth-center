package top.cloudlab.auth.domain.user;

import lombok.Getter;

/**
 * 实验室用户令牌
 */
@Getter
public class LabUserToken {
    private final String token;
    private final String expire;
    private final String userId;
    private final String partner;
    private final String sign;
    private final String answerToken;
    private final String answerUser;

    public static LabUserToken of(String token, String expire, String userId, String partner, String sign, String answerToken, String answerUser) {
        return new LabUserToken(token, expire, userId, partner, sign, answerToken, answerUser);
    }

    protected LabUserToken(String token, String expire, String userId, String partner, String sign, String answerToken, String answerUser) {
        this.token = token;
        this.expire = expire;
        this.userId = userId;
        this.partner = partner;
        this.sign = sign;
        this.answerToken = answerToken;
        this.answerUser = answerUser;
    }
}
