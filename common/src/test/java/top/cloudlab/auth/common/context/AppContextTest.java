package top.cloudlab.auth.common.context;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class AppContextTest {

    @AfterEach
    void tearDown() {
        AppContext.clear();
    }

    @Test
    void shouldSetValueWhenKeyAndValueAreNotNull() {
        AppContext.set("traceId", "abc-123");
        assertThat(AppContext.get("traceId")).isEqualTo("abc-123");
    }

    @Test
    void shouldReturnNullWhenKeyNotExists() {
        assertThat(AppContext.get("not-exist")).isNull();
    }

    @Test
    void shouldIgnoreWhenKeyIsNull() {
        AppContext.set(null, "value");
        assertThat(AppContext.get(null)).isNull();
    }

    @Test
    void shouldIgnoreWhenValueIsNull() {
        AppContext.set("key", null);
        assertThat(AppContext.get("key")).isNull();
    }

    @Test
    void shouldClearContext() {
        AppContext.set("traceId", "abc");
        AppContext.clear();
        assertThat(AppContext.get("traceId")).isNull();
    }

    @Test
    void shouldOverwriteExistingValue() {
        AppContext.set("key", "old");
        AppContext.set("key", "new");
        assertThat(AppContext.get("key")).isEqualTo("new");
    }

    @Test
    void shouldSupportMultipleKeys() {
        AppContext.set("traceId", "t1");
        AppContext.set("tenantId", "t2");
        assertThat(AppContext.get("traceId")).isEqualTo("t1");
        assertThat(AppContext.get("tenantId")).isEqualTo("t2");
    }
}
