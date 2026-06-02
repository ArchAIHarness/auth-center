package top.cloudlab.auth.infrastructure.utils;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;

/**
 * 方法引用解析器
 */
public class MethodReferenceResolver {

    private MethodReferenceResolver() {
    }

    private static final String REFERENCE_METHOD_NAME = "writeReplace";

    private static final String JAVA_GETTER_PREFIX = "get";
    private static final String JAVA_IS_PREFIX = "is";

    /**
     * 获取真实名称
     * 
     * @param <T>
     * @param <O>
     * @param getter
     * @return
     */
    public static <T, O> String realName(SerializableFunction<T, O> getter) {
        try {
            String reference = reference(getter);
            if (reference.startsWith(JAVA_GETTER_PREFIX)) {
                return decapitalize(reference.substring(JAVA_GETTER_PREFIX.length()));
            }
            if (reference.startsWith(JAVA_IS_PREFIX)) {
                return decapitalize(reference.substring(JAVA_IS_PREFIX.length()));
            }
            return reference;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取引用
     * 
     * @param <T>
     * @param <O>
     * @param reference
     * @return
     */
    private static <T, O> String reference(SerializableFunction<T, O> reference) {
        try {
            Method writeReplaceMethod = reference.getClass().getDeclaredMethod(REFERENCE_METHOD_NAME);
            writeReplaceMethod.setAccessible(true);
            SerializedLambda serializedLambda = (SerializedLambda) writeReplaceMethod.invoke(reference);
            return serializedLambda.getImplMethodName();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 解析
     * 
     * @param name
     * @return
     */
    private static String decapitalize(String name) {
        if (name == null || name.isEmpty()) {
            return name;
        }
        char firstChar = name.charAt(0);
        if (Character.isLowerCase(firstChar)) {
            return name;
        }
        // 处理特殊情况：如果第二个字符是大写，不改变首字母（如XMLName保持不变）
        if (name.length() > 1 && Character.isUpperCase(name.charAt(1))) {
            return name;
        }
        char[] chars = name.toCharArray();
        chars[0] = Character.toLowerCase(firstChar);
        return new String(chars);
    }

}
