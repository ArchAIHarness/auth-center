package top.cloudlab.auth.domain.access;

import top.cloudlab.auth.common.id.ID;

public record AccessTokenId(String value) {

    public static AccessTokenId of(String value) {
        return new AccessTokenId(value);
    }

    public static AccessTokenId generate() {
        return new AccessTokenId(new ID().getValue());
    }
}
