package top.cloudlab.auth.infrastructure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.lang.reflect.Method;


import org.junit.jupiter.api.Test;

import top.cloudlab.auth.infrastructure.utils.SpELUtil;

class SpELUtilTest {

    @SuppressWarnings("null")
    @Test
    void testGetWithPlaceholder() throws NoSuchMethodException {
        Method method = TestClass.class.getMethod("testMethod", String.class, Integer.class);
        Object[] args = new Object[]{"hello", 123};
        
        String expression = "#arg0";
        String result = SpELUtil.get(expression, method, args, String.class);
        
        assertEquals("hello", result);
    }

    @SuppressWarnings("null")
    @Test
    void testGetWithParamName() throws NoSuchMethodException {
        Method method = TestClass.class.getMethod("testMethod", String.class, Integer.class);
        Object[] args = new Object[]{"hello", 123};
        
        String expression = "#name";
        String result = SpELUtil.get(expression, method, args, String.class);
        
        assertEquals("hello", result);
    }

    @SuppressWarnings("null")
    @Test
    void testGetWithoutPlaceholder() throws NoSuchMethodException {
        Method method = TestClass.class.getMethod("testMethod", String.class, Integer.class);
        Object[] args = new Object[]{"hello", 123};
        
        String expression = "static-value";
        String result = SpELUtil.get(expression, method, args, String.class);
        
        assertEquals("static-value", result);
    }

    @SuppressWarnings("null")
    @Test
    void testGetWithNumericConversion() throws NoSuchMethodException {
        Method method = TestClass.class.getMethod("testMethod", String.class, Integer.class);
        Object[] args = new Object[]{"hello", 123};
        
        String expression = "#arg1";
        Integer result = SpELUtil.get(expression, method, args, Integer.class);
        
        assertEquals(123, result);
    }

    @SuppressWarnings("null")
    @Test
    void testGetWithNullArgs() throws NoSuchMethodException {
        Method method = TestClass.class.getMethod("testMethod", String.class, Integer.class);
        
        String expression = "#arg0";
        String result = SpELUtil.get(expression, method, null, String.class);
        
        assertNull(result);
    }

    @SuppressWarnings("null")
    @Test
    void testGetWithEmptyExpression() throws NoSuchMethodException {
        Method method = TestClass.class.getMethod("testMethod", String.class, Integer.class);
        Object[] args = new Object[]{"hello", 123};
        
        String result = SpELUtil.get("", method, args, String.class);
        
        assertEquals("", result);
    }

    @SuppressWarnings("null")
    @Test
    void testGetWithP0Variable() throws NoSuchMethodException {
        Method method = TestClass.class.getMethod("testMethod", String.class, Integer.class);
        Object[] args = new Object[]{"hello", 123};
        
        String expression = "#p0";
        String result = SpELUtil.get(expression, method, args, String.class);
        
        assertEquals("hello", result);
    }

    @SuppressWarnings("null")
    @Test
    void testGetWithA0Variable() throws NoSuchMethodException {
        Method method = TestClass.class.getMethod("testMethod", String.class, Integer.class);
        Object[] args = new Object[]{"hello", 123};
        
        String expression = "#a0";
        String result = SpELUtil.get(expression, method, args, String.class);
        
        assertEquals("hello", result);
    }

    @SuppressWarnings("null")
    @Test
    void testGetWithArgsVariable() throws NoSuchMethodException {
        Method method = TestClass.class.getMethod("testMethod", String.class, Integer.class);
        Object[] args = new Object[]{"hello", 123};
        
        String expression = "#args[0]";
        String result = SpELUtil.get(expression, method, args, String.class);
        
        assertEquals("hello", result);
    }

    static class TestClass {
        public void testMethod(String name, Integer age) {
        }
    }
}
