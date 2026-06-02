package top.cloudlab.auth.common.context;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class RequestContextTest {

    @AfterEach
    void tearDown() {
        RequestContext.clear();
    }

    @Test
    void shouldSetAndGetHeader() {
        RequestContext.set("X-Trace-Id", "trace-123");
        assertThat(RequestContext.get("X-Trace-Id")).isEqualTo("trace-123");
    }

    @Test
    void shouldReturnNullWhenHeaderNotExists() {
        assertThat(RequestContext.get("X-Not-Exist")).isNull();
    }

    @Test
    void shouldSetAllHeaders() {
        RequestContext.setAll(Map.of("key1", "val1", "key2", "val2"));
        assertThat(RequestContext.get("key1")).isEqualTo("val1");
        assertThat(RequestContext.get("key2")).isEqualTo("val2");
    }

    @Test
    void shouldGetAllHeaders() {
        RequestContext.set("key1", "val1");
        RequestContext.set("key2", "val2");
        Map<String, String> all = RequestContext.getAll();
        assertThat(all).hasSize(2);
        assertThat(all.get("key1")).isEqualTo("val1");
        assertThat(all.get("key2")).isEqualTo("val2");
    }

    @Test
    void shouldReturnEmptyMapWhenNoHeadersSet() {
        assertThat(RequestContext.getAll()).isEmpty();
    }

    @Test
    void shouldClearContext() {
        RequestContext.set("key", "val");
        RequestContext.clear();
        assertThat(RequestContext.get("key")).isNull();
    }

    @Test
    void shouldOverwriteExistingHeader() {
        RequestContext.set("key", "old");
        RequestContext.set("key", "new");
        assertThat(RequestContext.get("key")).isEqualTo("new");
    }
}
