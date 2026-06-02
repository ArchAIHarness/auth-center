package top.cloudlab.auth.infrastructure.jpa.converter;

import jakarta.persistence.AttributeConverter;
import top.cloudlab.auth.domain.access.TokenType;

public class TokenTypeConverter implements AttributeConverter<TokenType, Integer> {

    @Override
    public Integer convertToDatabaseColumn(TokenType tokenType) {
        return tokenType == null ? null : tokenType.getCode();
    }

    @Override
    public TokenType convertToEntityAttribute(Integer code) {
        return code == null ? null : TokenType.fromCode(code);
    }
}