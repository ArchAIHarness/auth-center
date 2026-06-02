package top.cloudlab.auth.domain.user;

import lombok.Getter;

/**
 * 实验室用户登录信息
 */
@Getter
public class LabUserLogin {
    private final String labCode;
    private final String thirdUserId;
    private final String nickname;
    private final String avatar;
    private final String phone;
    private final Integer roleCode;

    public static LabUserLogin of(String labCode, String thirdUserId, String nickname, String avatar, String phone, Integer roleCode) {
        return new LabUserLogin(labCode, thirdUserId, nickname, avatar, phone, roleCode);
    }

    protected LabUserLogin(String labCode, String thirdUserId, String nickname, String avatar, String phone, Integer roleCode) {
        this.labCode = labCode;
        this.thirdUserId = thirdUserId;
        this.nickname = nickname;
        this.avatar = avatar;
        this.phone = phone;
        this.roleCode = roleCode;
    }
}
