package top.cloudlab.auth.common.pagination;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.cloudlab.auth.common.exception.DomainException;

/**
 * 分页查询参数
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageQuery {

    /**
     * 当前页
     */
    private Integer page;

    /**
     * 每页数
     */
    private Integer size;

    /**
     * 关键词
     */
    private String keywords;

    /**
     * 校验分页参数
     */
    public void validate() {
        if (page == null) {
            throw DomainException.of("INVALID_PARAM", "当前页不能为空");
        }
        if (page < 1) {
            throw DomainException.of("INVALID_PARAM", "当前页不能小于1");
        }
        if (size == null) {
            throw DomainException.of("INVALID_PARAM", "每页数不能为空");
        }
        if (size < 1) {
            throw DomainException.of("INVALID_PARAM", "每页数不能小于1");
        }
        if (size > 500) {
            throw DomainException.of("INVALID_PARAM", "每页数不能超过500个");
        }
    }

    /**
     * 计算 offset（从第几条开始）
     *
     * @return offset
     */
    public long getOffset() {
        if (page == null || size == null) {
            return 0L;
        }
        return (long) (page - 1) * size;
    }
}