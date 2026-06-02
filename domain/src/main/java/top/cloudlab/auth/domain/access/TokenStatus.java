package top.cloudlab.auth.domain.access;

import top.cloudlab.auth.common.base.ValueObject;

public enum TokenStatus implements ValueObject {

    VALID(0, "有效"),
    EXPIRED(1, "已过期"),
    REVOKED(2, "已撤销"),
    ;

    private final int code;
    private final String desc;

    TokenStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static TokenStatus fromCode(int code) {
        for (TokenStatus status : values()) {
            if (status.code == code) return status;
        }
        throw new IllegalArgumentException("Unknown TokenStatus code: " + code);
    }
}