package top.cloudlab.auth.infrastructure;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import top.cloudlab.auth.infrastructure.utils.SerializableFunction;
import top.cloudlab.auth.infrastructure.utils.MethodReferenceResolver;

class MethodReferenceResolverTest {

    @Test
    void testRealNameWithGetter() {
        SerializableFunction<TestClass, String> getter = TestClass::getName;
        String result = MethodReferenceResolver.realName(getter);
        assertEquals("name", result);
    }

    @Test
    void testRealNameWithIsPrefix() {
        SerializableFunction<TestClass, Boolean> getter = TestClass::isActive;
        String result = MethodReferenceResolver.realName(getter);
        assertEquals("active", result);
    }

    @Test
    void testRealNameWithFieldDirectly() {
        SerializableFunction<TestClass, String> getter = TestClass::getValue;
        String result = MethodReferenceResolver.realName(getter);
        assertEquals("value", result);
    }

    static class TestClass {
        private String name;
        private boolean active;
        private String value;

        public String getName() {
            return name;
        }

        public boolean isActive() {
            return active;
        }

        public String getValue() {
            return value;
        }
    }
}
