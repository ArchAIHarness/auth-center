package top.cloudlab.auth.common.id;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

class IDSerializationTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldSerializeIdAsString() throws Exception {
        ID id = new ID("test-id-value");
        String json = objectMapper.writeValueAsString(id);
        assertThat(json).isEqualTo("\"test-id-value\"");
    }

    @Test
    void shouldDeserializeStringToId() throws Exception {
        ID id = objectMapper.readValue("\"test-id-value\"", ID.class);
        assertThat(id.getValue()).isEqualTo("test-id-value");
    }

    @Test
    void shouldRoundTripSerializeAndDeserialize() throws Exception {
        ID original = new ID("round-trip-id");
        String json = objectMapper.writeValueAsString(original);
        ID deserialized = objectMapper.readValue(json, ID.class);
        assertThat(deserialized).isEqualTo(original);
    }
}
