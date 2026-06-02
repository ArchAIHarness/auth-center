package top.cloudlab.auth.common.pagination;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import top.cloudlab.auth.common.exception.DomainException;

class PageQueryTest {

    @Test
    void shouldCalculateOffsetCorrectly() {
        PageQuery query = PageQuery.builder().page(2).size(10).build();
        assertThat(query.getOffset()).isEqualTo(10L);
    }

    @Test
    void shouldReturnZeroOffsetWhenPageIsNull() {
        PageQuery query = PageQuery.builder().page(null).size(10).build();
        assertThat(query.getOffset()).isEqualTo(0L);
    }

    @Test
    void shouldReturnZeroOffsetWhenSizeIsNull() {
        PageQuery query = PageQuery.builder().page(1).size(null).build();
        assertThat(query.getOffset()).isEqualTo(0L);
    }

    @Test
    void shouldReturnZeroOffsetForFirstPage() {
        PageQuery query = PageQuery.builder().page(1).size(20).build();
        assertThat(query.getOffset()).isEqualTo(0L);
    }

    @Test
    void shouldPassValidationWhenValid() {
        PageQuery query = PageQuery.builder().page(1).size(10).build();
        query.validate();
    }

    @Test
    void shouldThrowWhenPageIsNull() {
        PageQuery query = PageQuery.builder().page(null).size(10).build();
        assertThatThrownBy(query::validate)
                .isInstanceOf(DomainException.class);
    }

    @Test
    void shouldThrowWhenPageLessThanOne() {
        PageQuery query = PageQuery.builder().page(0).size(10).build();
        assertThatThrownBy(query::validate)
                .isInstanceOf(DomainException.class);
    }

    @Test
    void shouldThrowWhenSizeIsNull() {
        PageQuery query = PageQuery.builder().page(1).size(null).build();
        assertThatThrownBy(query::validate)
                .isInstanceOf(DomainException.class);
    }

    @Test
    void shouldThrowWhenSizeLessThanOne() {
        PageQuery query = PageQuery.builder().page(1).size(0).build();
        assertThatThrownBy(query::validate)
                .isInstanceOf(DomainException.class);
    }

    @Test
    void shouldThrowWhenSizeExceeds500() {
        PageQuery query = PageQuery.builder().page(1).size(501).build();
        assertThatThrownBy(query::validate)
                .isInstanceOf(DomainException.class);
    }

    @Test
    void shouldAcceptSize500() {
        PageQuery query = PageQuery.builder().page(1).size(500).build();
        query.validate();
    }
}
