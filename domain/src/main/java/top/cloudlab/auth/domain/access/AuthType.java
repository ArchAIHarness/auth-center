package top.cloudlab.auth.domain.access;

import top.cloudlab.auth.common.base.ValueObject;

public enum AuthType implements ValueObject {

    Auth(0, "平台认证"),
    OAuth(1, "OAuth认证"),
    SSO(2, "SSO单点登录"),
    ;

    private final int code;
    private final String desc;

    AuthType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static AuthType fromCode(int code) {
        for (AuthType type : values()) {
            if (type.code == code) return type;
        }
        throw new IllegalArgumentException("Unknown AuthType code: " + code);
    }
}