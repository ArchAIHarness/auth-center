package top.cloudlab.auth.common.pagination;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

class PageResultTest {

    @Test
    void shouldCalculateTotalPages() {
        PageResult<String> result = PageResult.<String>builder()
                .total(25L).page(1).size(10).rows(List.of()).build();
        assertThat(result.getPages()).isEqualTo(3);
    }

    @Test
    void shouldReturnZeroPagesWhenTotalIsNull() {
        PageResult<String> result = PageResult.<String>builder()
                .total(null).page(1).size(10).rows(List.of()).build();
        assertThat(result.getPages()).isEqualTo(0);
    }

    @Test
    void shouldReturnZeroPagesWhenSizeIsZero() {
        PageResult<String> result = PageResult.<String>builder()
                .total(25L).page(1).size(0).rows(List.of()).build();
        assertThat(result.getPages()).isEqualTo(0);
    }

    @Test
    void shouldReturnZeroPagesWhenSizeIsNull() {
        PageResult<String> result = PageResult.<String>builder()
                .total(25L).page(1).size(null).rows(List.of()).build();
        assertThat(result.getPages()).isEqualTo(0);
    }

    @Test
    void shouldReturnTrueForHasPreWhenPageGreaterThanOne() {
        PageResult<String> result = PageResult.<String>builder()
                .total(25L).page(2).size(10).rows(List.of()).build();
        assertThat(result.getHasPre()).isTrue();
    }

    @Test
    void shouldReturnFalseForHasPreWhenPageIsOne() {
        PageResult<String> result = PageResult.<String>builder()
                .total(25L).page(1).size(10).rows(List.of()).build();
        assertThat(result.getHasPre()).isFalse();
    }

    @Test
    void shouldReturnFalseForHasPreWhenPageIsZero() {
        PageResult<String> result = PageResult.<String>builder()
                .total(25L).page(0).size(10).rows(List.of()).build();
        assertThat(result.getHasPre()).isFalse();
    }

    @Test
    void shouldReturnTrueForHasNextWhenMorePages() {
        PageResult<String> result = PageResult.<String>builder()
                .total(25L).page(1).size(10).rows(List.of()).build();
        assertThat(result.getHasNext()).isTrue();
    }

    @Test
    void shouldReturnFalseForHasNextWhenLastPage() {
        PageResult<String> result = PageResult.<String>builder()
                .total(25L).page(3).size(10).rows(List.of()).build();
        assertThat(result.getHasNext()).isFalse();
    }

    @Test
    void shouldReturnFalseForHasNextWhenPageIsZero() {
        PageResult<String> result = PageResult.<String>builder()
                .total(25L).page(0).size(10).rows(List.of()).build();
        assertThat(result.getHasNext()).isFalse();
    }

    @Test
    void shouldCreateEmptyResult() {
        PageResult<String> result = PageResult.empty();
        assertThat(result.getTotal()).isEqualTo(0L);
        assertThat(result.getPage()).isEqualTo(1);
        assertThat(result.getSize()).isEqualTo(10);
        assertThat(result.getRows()).isEmpty();
        assertThat(result.getHasPre()).isFalse();
        assertThat(result.getHasNext()).isFalse();
    }
}
