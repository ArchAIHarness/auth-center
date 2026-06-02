package top.cloudlab.auth.common.exception;

import lombok.Getter;

/**
 * 领域异常
 * 用于表示领域层的业务异常
 */
@Getter
public class DomainException extends RuntimeException {

    private final Integer code;
    private final String message;

    private DomainException(Integer code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
    }

    public DomainException(Integer code, String message) {
        this(code, message, null);
    }

    public static DomainException of(String code, String message) {
        return new DomainException(403, message);
    }

    public static DomainException notFound(String entity, String id) {
        return new DomainException(404, entity + " not found: " + id);
    }

    public static DomainException alreadyExists(String entity, String identifier) {
        return new DomainException(409, entity + " already exists: " + identifier);
    }

    public static DomainException invalidState(String message) {
        return new DomainException(400, "Invalid state: " + message);
    }
}
