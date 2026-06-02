package top.cloudlab.auth.interfaces.config.exception;

import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import top.cloudlab.auth.common.dto.R;
import top.cloudlab.auth.common.exception.DomainException;

/**
 * 全局异常处理
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionAdvice {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error(e.getMessage(), e);
        R<?> r = R.builder()
                .code(400)
                .message(e.getMessage())
                .build();
        return ResponseEntity.status(400).body(r);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleConstraintViolationException(ConstraintViolationException e) {
        Set<ConstraintViolation<?>> set = e.getConstraintViolations();
        String message = set.stream()
                .map(ConstraintViolation::getMessage)
                .findFirst()
                .orElse("参数错误");
        log.error(e.getMessage(), e);
        R<?> r = R.builder()
                .code(400)
                .message(message)
                .build();
        return ResponseEntity.status(400).body(r);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<?> handleUnauthorizedException(UnauthorizedException e) {
        log.error(e.getMessage(), e);
        R<?> r = R.builder()
                .code(401)
                .message(e.getMessage())
                .build();
        return ResponseEntity.status(401).body(r);
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<?> handleDomainException(DomainException e) {
        log.error("领域异常: {} - {}", e.getCode(), e.getMessage());
        Integer code = e.getCode();
        int status = 400;
        if (code != null && code == 401) {
            status = 401;
        } else if (code != null && code == 403) {
            status = 403;
        }
        R<?> r = R.<Void>builder()
                .code(code)
                .message(e.getMessage())
                .build();
        return ResponseEntity.status(status).body(r);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception e) {
        log.error("系统异常: {}", e.getClass().getName(), e);
        String message = e.getMessage();
        if (message != null && message.contains("expired")) {
            R<?> r = R.builder().code(401).message("令牌已过期").build();
            return ResponseEntity.status(401).body(r);
        }
        R<?> r = R.builder().code(500).message("系统内部错误").build();
        return ResponseEntity.status(500).body(r);
    }

}
