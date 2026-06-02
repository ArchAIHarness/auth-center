package top.cloudlab.auth.infrastructure;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import top.cloudlab.auth.infrastructure.utils.TemplateUtil;

class TemplateUtilTest {

    @Test
    void testRenderWithMap() {
        Map<String, Object> data = new HashMap<>();
        data.put("name", "John");
        data.put("age", 30);
        
        String template = "Name: ${name}, Age: ${age}";
        String result = TemplateUtil.render(template, data);
        
        assertEquals("Name: John, Age: 30", result);
    }

    @Test
    void testRenderWithNestedMap() {
        Map<String, Object> data = new HashMap<>();
        Map<String, Object> user = new HashMap<>();
        user.put("name", "John");
        user.put("age", 30);
        data.put("user", user);
        
        String template = "Name: ${user.name}, Age: ${user.age}";
        String result = TemplateUtil.render(template, data);
        
        assertEquals("Name: John, Age: 30", result);
    }

    @Test
    void testRenderWithList() {
        List<String> data = List.of("John", "30", "Beijing");
        
        String template = "Name: ${0}, Age: ${1}, City: ${2}";
        String result = TemplateUtil.render(template, data);
        
        assertEquals("Name: John, Age: 30, City: Beijing", result);
    }

    @Test
    void testRenderWithObject() {
        TestData data = new TestData("John", 30);
        
        String template = "Name: ${name}, Age: ${age}";
        String result = TemplateUtil.render(template, data);
        
        assertEquals("Name: John, Age: 30", result);
    }

    @Test
    void testRenderWithNestedObject() {
        NestedData data = new NestedData(new User("John", 30));
        
        String template = "Name: ${user.name}, Age: ${user.age}";
        String result = TemplateUtil.render(template, data);
        
        assertEquals("Name: John, Age: 30", result);
    }

    @Test
    void testRenderWithNullValue() {
        Map<String, Object> data = new HashMap<>();
        data.put("name", "John");
        data.put("age", null);
        
        String template = "Name: ${name}, Age: ${age}";
        String result = TemplateUtil.render(template, data);
        
        assertEquals("Name: John, Age: ", result);
    }

    @Test
    void testRenderWithEmptyTemplate() {
        Map<String, Object> data = new HashMap<>();
        data.put("name", "John");
        
        String result = TemplateUtil.render("", data);
        assertEquals("", result);
    }

    @Test
    void testRenderWithNoPlaceholder() {
        Map<String, Object> data = new HashMap<>();
        data.put("name", "John");
        
        String result = TemplateUtil.render("Hello World", data);
        assertEquals("Hello World", result);
    }

    @Test
    void testRenderWithNullData() {
        String template = "Hello ${name}";
        String result = TemplateUtil.render(template, (Map<String, Object>) null);
        assertEquals("Hello ", result);
    }

    @Test
    void testRenderListWithMoreVariables() {
        List<String> data = List.of("a", "b");
        
        String template = "${0}${1}${2}";
        String result = TemplateUtil.render(template, data);
        
        assertEquals("ab", result);
    }

    static class TestData {
        private String name;
        private int age;

        public TestData(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() { return name; }
        public int getAge() { return age; }
    }

    static class NestedData {
        private User user;

        public NestedData(User user) {
            this.user = user;
        }

        public User getUser() { return user; }
    }

    static class User {
        private String name;
        private int age;

        public User(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() { return name; }
        public int getAge() { return age; }
    }
}
