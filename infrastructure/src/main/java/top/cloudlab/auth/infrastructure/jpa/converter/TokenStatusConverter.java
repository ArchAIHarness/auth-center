package top.cloudlab.auth.infrastructure.jpa.converter;

import jakarta.persistence.AttributeConverter;
import top.cloudlab.auth.domain.access.TokenStatus;

public class TokenStatusConverter implements AttributeConverter<TokenStatus, Integer> {

    @Override
    public Integer convertToDatabaseColumn(TokenStatus tokenStatus) {
        return tokenStatus == null ? null : tokenStatus.getCode();
    }

    @Override
    public TokenStatus convertToEntityAttribute(Integer code) {
        return code == null ? null : TokenStatus.fromCode(code);
    }
}