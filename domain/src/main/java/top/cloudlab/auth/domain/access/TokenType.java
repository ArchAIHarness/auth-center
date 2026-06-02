package top.cloudlab.auth.domain.access;

import top.cloudlab.auth.common.base.ValueObject;

public enum TokenType implements ValueObject {

    Basic(0, "基础令牌"),
    Bearer(1, "Bearer令牌"),
    ApiKey(2, "API密钥"),
    ;

    private final int code;
    private final String desc;

    TokenType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static TokenType fromCode(int code) {
        for (TokenType type : values()) {
            if (type.code == code) return type;
        }
        throw new IllegalArgumentException("Unknown TokenType code: " + code);
    }
}