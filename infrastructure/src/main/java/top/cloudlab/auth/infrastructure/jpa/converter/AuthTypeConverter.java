package top.cloudlab.auth.infrastructure.jpa.converter;

import jakarta.persistence.AttributeConverter;
import top.cloudlab.auth.domain.access.AuthType;

public class AuthTypeConverter implements AttributeConverter<AuthType, Integer> {

    @Override
    public Integer convertToDatabaseColumn(AuthType authType) {
        return authType == null ? null : authType.getCode();
    }

    @Override
    public AuthType convertToEntityAttribute(Integer code) {
        return code == null ? null : AuthType.fromCode(code);
    }
}