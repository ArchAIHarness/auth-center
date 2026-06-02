package top.cloudlab.auth.domain.oauth;

import top.cloudlab.auth.common.base.ValueObject;

public enum CodeStatus implements ValueObject {

    VALID(0, "有效"),
    USED(1, "已使用"),
    EXPIRED(2, "已过期"),
    ;

    private final int code;
    private final String desc;

    CodeStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static CodeStatus fromCode(int code) {
        for (CodeStatus status : values()) {
            if (status.code == code) return status;
        }
        throw new IllegalArgumentException("Unknown CodeStatus code: " + code);
    }
}