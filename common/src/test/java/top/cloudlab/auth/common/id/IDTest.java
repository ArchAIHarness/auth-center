package top.cloudlab.auth.common.id;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class IDTest {

    @Test
    void shouldGenerateRandomIdWhenDefaultConstructor() {
        ID id = new ID();
        assertThat(id.getValue()).isNotBlank();
    }

    @Test
    void shouldCreateIdFromValue() {
        ID id = new ID("test-id");
        assertThat(id.getValue()).isEqualTo("test-id");
    }

    @Test
    void shouldThrowWhenValueIsNull() {
        assertThatThrownBy(() -> new ID(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldThrowWhenValueIsEmpty() {
        assertThatThrownBy(() -> new ID(""))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldGenerateIdWithSpecifiedLength() {
        String generated = ID.generate(16);
        assertThat(generated).isNotBlank();
    }

    @Test
    void shouldGenerateUniqueIds() {
        ID id1 = new ID();
        ID id2 = new ID();
        assertThat(id1.getValue()).isNotEqualTo(id2.getValue());
    }

    @Test
    void shouldEqualWhenSameValue() {
        ID id1 = new ID("same");
        ID id2 = new ID("same");
        assertThat(id1).isEqualTo(id2);
        assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
    }

    @Test
    void shouldNotEqualWhenDifferentValue() {
        ID id1 = new ID("a");
        ID id2 = new ID("b");
        assertThat(id1).isNotEqualTo(id2);
    }

    @Test
    void shouldReturnValueOnToString() {
        ID id = new ID("abc");
        assertThat(id.toString()).isEqualTo("abc");
    }

    @Test
    void shouldCompareCorrectly() {
        ID id1 = new ID("a");
        ID id2 = new ID("b");
        assertThat(id1.compareTo(id2)).isNegative();
        assertThat(id2.compareTo(id1)).isPositive();
    }

    @Test
    void shouldNotEqualNull() {
        ID id = new ID("test");
        assertThat(id.equals(null)).isFalse();
    }

    @Test
    @SuppressWarnings("unlikely-arg-type")
    void shouldNotEqualDifferentType() {
        ID id = new ID("test");
        assertThat(id.equals("test")).isFalse();
    }
}
