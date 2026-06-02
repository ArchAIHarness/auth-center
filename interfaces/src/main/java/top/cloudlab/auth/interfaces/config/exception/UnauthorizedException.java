package top.cloudlab.auth.interfaces.config.exception;

public class UnauthorizedException extends RuntimeException {

    // 无参构造
    public UnauthorizedException() {
        super();
    }

    // 带消息的构造（最常用）
    public UnauthorizedException(String message) {
        super(message);
    }

    // 带消息+异常原因
    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}
