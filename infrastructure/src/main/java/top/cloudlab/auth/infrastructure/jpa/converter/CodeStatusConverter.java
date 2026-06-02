package top.cloudlab.auth.infrastructure.jpa.converter;

import jakarta.persistence.AttributeConverter;
import top.cloudlab.auth.domain.oauth.CodeStatus;

public class CodeStatusConverter implements AttributeConverter<CodeStatus, Integer> {

    @Override
    public Integer convertToDatabaseColumn(CodeStatus codeStatus) {
        return codeStatus == null ? null : codeStatus.getCode();
    }

    @Override
    public CodeStatus convertToEntityAttribute(Integer code) {
        return code == null ? null : CodeStatus.fromCode(code);
    }
}