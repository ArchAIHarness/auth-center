package top.cloudlab.auth.common.context;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class UserContextTest {

    @AfterEach
    void tearDown() {
        UserContext.clear();
    }

    @Test
    void shouldSetAndGetUserId() {
        UserContext.setUserId("user-123");
        assertThat(UserContext.getUserId()).isEqualTo("user-123");
    }

    @Test
    void shouldReturnNullWhenUserIdNotSet() {
        assertThat(UserContext.getUserId()).isNull();
    }

    @Test
    void shouldReturnEmptyOptionalWhenUserIdNotSet() {
        assertThat(UserContext.getUserIdOptional()).isEqualTo(Optional.empty());
    }

    @Test
    void shouldReturnPresentOptionalWhenUserIdSet() {
        UserContext.setUserId("user-123");
        assertThat(UserContext.getUserIdOptional()).isPresent().contains("user-123");
    }

    @Test
    void shouldSetAndGetTenantId() {
        UserContext.setTenantId("tenant-456");
        assertThat(UserContext.getTenantId()).isEqualTo("tenant-456");
    }

    @Test
    void shouldReturnNullWhenTenantIdNotSet() {
        assertThat(UserContext.getTenantId()).isNull();
    }

    @Test
    void shouldReturnEmptyOptionalWhenTenantIdNotSet() {
        assertThat(UserContext.getTenantIdOptional()).isEqualTo(Optional.empty());
    }

    @Test
    void shouldReturnPresentOptionalWhenTenantIdSet() {
        UserContext.setTenantId("tenant-456");
        assertThat(UserContext.getTenantIdOptional()).isPresent().contains("tenant-456");
    }

    @Test
    void shouldClearBothUserIdAndTenantId() {
        UserContext.setUserId("user-1");
        UserContext.setTenantId("tenant-1");
        UserContext.clear();
        assertThat(UserContext.getUserId()).isNull();
        assertThat(UserContext.getTenantId()).isNull();
    }
}
