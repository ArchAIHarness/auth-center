package top.cloudlab.auth.common.id;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * ID 类型
 * 用于生成和表示唯一标识符
 */
@JsonSerialize(using = ID.Serializer.class)
@JsonDeserialize(using = ID.Deserializer.class)
public class ID implements Comparable<ID> {

    private static final SecureRandom random = new SecureRandom();
    private static final Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();

    private final String value;

    public ID() {
        this.value = generate();
    }

    public ID(String value) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("ID value cannot be null or empty");
        }
        this.value = value;
    }

    /**
     * 生成随机 ID
     *
     * @return ID 字符串
     */
    private static String generate() {
        byte[] bytes = new byte[12];
        random.nextBytes(bytes);
        return encoder.encodeToString(bytes);
    }

    /**
     * 生成指定长度的随机 ID
     *
     * @param bytesLength 字节长度
     * @return ID 字符串
     */
    public static String generate(int bytesLength) {
        byte[] bytes = new byte[bytesLength];
        random.nextBytes(bytes);
        return encoder.encodeToString(bytes);
    }

    /**
     * 获取 ID 值
     *
     * @return ID 字符串
     */
    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ID other = (ID) obj;
        return value.equals(other.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public int compareTo(ID other) {
        return this.value.compareTo(other.value);
    }

    /**
     * 序列化器
     */
    public static class Serializer extends JsonSerializer<ID> {
        @Override
        public void serialize(ID id, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeString(id.getValue());
        }
    }

    /**
     * 反序列化器
     */
    public static class Deserializer extends JsonDeserializer<ID> {
        @Override
        public ID deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            String value = p.getValueAsString();
            return new ID(value);
        }
    }
}
