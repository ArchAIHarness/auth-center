package top.cloudlab.auth.common.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class DomainExceptionTest {

    @Test
    void shouldCreateExceptionWithOf() {
        DomainException ex = DomainException.of("INVALID_PARAM", "参数错误");
        assertThat(ex.getCode()).isEqualTo(403);
        assertThat(ex.getMessage()).isEqualTo("参数错误");
    }

    @Test
    void shouldCreateNotFoundException() {
        DomainException ex = DomainException.notFound("User", "123");
        assertThat(ex.getCode()).isEqualTo(404);
        assertThat(ex.getMessage()).isEqualTo("User not found: 123");
    }

    @Test
    void shouldCreateAlreadyExistsException() {
        DomainException ex = DomainException.alreadyExists("User", "abc");
        assertThat(ex.getCode()).isEqualTo(409);
        assertThat(ex.getMessage()).isEqualTo("User already exists: abc");
    }

    @Test
    void shouldCreateInvalidStateException() {
        DomainException ex = DomainException.invalidState("invalid transition");
        assertThat(ex.getCode()).isEqualTo(400);
        assertThat(ex.getMessage()).isEqualTo("Invalid state: invalid transition");
    }

    @Test
    void shouldCreateWithCodeAndMessage() {
        DomainException ex = DomainException.of("TEST_CODE", "internal error");
        assertThat(ex.getCode()).isEqualTo(403);
        assertThat(ex.getMessage()).isEqualTo("internal error");
    }

    @Test
    void shouldPreserveCauseWhenThrown() {
        DomainException ex = new DomainException(500, "internal error");
        assertThat(ex.getCode()).isEqualTo(500);
        assertThat(ex.getMessage()).isEqualTo("internal error");
        assertThat(ex.getCause()).isNull();
    }

    @Test
    void shouldThrowDomainException() {
        assertThatThrownBy(() -> { throw DomainException.of("CODE", "msg"); })
                .isInstanceOf(DomainException.class)
                .satisfies(e -> {
                    DomainException de = (DomainException) e;
                    assertThat(de.getCode()).isEqualTo(403);
                    assertThat(de.getMessage()).isEqualTo("msg");
                });
    }
}
