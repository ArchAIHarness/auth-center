package top.cloudlab.auth.common.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class RTest {

    @Test
    void shouldReturnSuccessWhenCodeIsZero() {
        R<String> r = R.<String>builder().code(0).data("ok").build();
        assertThat(r.getSuccess()).isTrue();
    }

    @Test
    void shouldReturnNotSuccessWhenCodeIsNonZero() {
        R<String> r = R.<String>builder().code(403).message("forbidden").build();
        assertThat(r.getSuccess()).isFalse();
    }

    @Test
    void shouldBuildWithAllFields() {
        R<String> r = R.<String>builder().code(0).message("success").data("payload").build();
        assertThat(r.getCode()).isEqualTo(0);
        assertThat(r.getMessage()).isEqualTo("success");
        assertThat(r.getData()).isEqualTo("payload");
    }

    @Test
    void shouldBuildWithCodeAndDataOnly() {
        R<String> r = R.<String>builder().code(0).data("payload").build();
        assertThat(r.getCode()).isEqualTo(0);
        assertThat(r.getData()).isEqualTo("payload");
        assertThat(r.getMessage()).isNull();
    }
}
