package top.cloudlab.auth.common.pagination;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {

    /** 总记录数 */
    private Long total;

    /** 当前页 */
    private Integer page;

    /** 每页条数 */
    private Integer size;

    /** 总页数 */
    private Integer pages;

    /** 是否有上一页 */
    private Boolean hasPre;

    /** 是否有下一页 */
    private Boolean hasNext;

    /** 数据列表 */
    private List<T> rows;

    public int getPages() {
        if (pages != null) {
            return pages;
        }
        if (size == null || size == 0 || total == null) {
            return 0;
        }
        return (int) Math.ceil((double) total / size);
    }

    public boolean getHasPre() {
        if (hasPre != null) {
            return hasPre;
        }
        return page != null && page > 1;
    }

    public boolean getHasNext() {
        if (hasNext != null) {
            return hasNext;
        }
        if (page == null || size == null || total == null) {
            return false;
        }
        int totalPages = getPages();
        return page > 0 && page < totalPages;
    }

    public static <T> PageResult<T> empty() {
        return PageResult.<T>builder()
                .total(0L)
                .page(1)
                .size(10)
                .pages(0)
                .hasPre(false)
                .hasNext(false)
                .rows(List.of())
                .build();
    }
}