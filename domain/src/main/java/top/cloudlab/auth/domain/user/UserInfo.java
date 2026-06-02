package top.cloudlab.auth.domain.user;

import lombok.Builder;
import lombok.Getter;

/**
 * 用户信息
 */
@Builder
@Getter
public class UserInfo {
    private final String userId;
    private final String nickname;
    private final String avatar;
    private final String phone;

    public static UserInfo of(String userId, String nickname, String avatar, String phone) {
        return new UserInfo(userId, nickname, avatar, phone);
    }

    protected UserInfo(String userId, String nickname, String avatar, String phone) {
        this.userId = userId;
        this.nickname = nickname;
        this.avatar = avatar;
        this.phone = phone;
    }
}
