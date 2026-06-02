package top.cloudlab.auth.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import top.cloudlab.auth.domain.user.UserInfo;

class UserInfoTest {

    @Test
    void testBuilder() {
        UserInfo userInfo = UserInfo.of("user-123", "TestUser", "http://example.com/avatar.png", "13800138000");

        assertNotNull(userInfo);
        assertEquals("user-123", userInfo.getUserId());
        assertEquals("TestUser", userInfo.getNickname());
        assertEquals("http://example.com/avatar.png", userInfo.getAvatar());
        assertEquals("13800138000", userInfo.getPhone());
    }

    @Test
    void testBuilderWithPartialFields() {
        UserInfo userInfo = UserInfo.of("user-456", "AnotherUser", null, null);

        assertNotNull(userInfo);
        assertEquals("user-456", userInfo.getUserId());
        assertEquals("AnotherUser", userInfo.getNickname());
        assertEquals(null, userInfo.getAvatar());
        assertEquals(null, userInfo.getPhone());
    }

    @Test
    void testBuilderCopy() {
        UserInfo original = UserInfo.of("user-123", "TestUser", null, "13800138000");

        UserInfo copied = UserInfo.of(original.getUserId(), "UpdatedUser", original.getAvatar(), original.getPhone());

        assertEquals("user-123", copied.getUserId());
        assertEquals("UpdatedUser", copied.getNickname());
        assertEquals("13800138000", copied.getPhone());
    }
}
