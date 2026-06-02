package top.cloudlab.auth.infrastructure.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.nio.charset.StandardCharsets;

@SuppressWarnings("null")
@Configuration
public class RedisConfig {

    static {
        ParserConfig.getGlobalInstance().addAccept("top.cloudlab.");
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        FastJsonRedisSerializer jsonSerializer = new FastJsonRedisSerializer();

        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);
        template.afterPropertiesSet();
        return template;
    }

    static class FastJsonRedisSerializer implements RedisSerializer<Object> {
        @Override
        public byte[] serialize(Object o) throws SerializationException {
            if (o == null) return new byte[0];
            return JSON.toJSONString(o, SerializerFeature.WriteClassName).getBytes(StandardCharsets.UTF_8);
        }

        @Override
        public Object deserialize(byte[] bytes) throws SerializationException {
            if (bytes == null || bytes.length == 0) return null;
            return JSON.parse(new String(bytes, StandardCharsets.UTF_8));
        }
    }
}
