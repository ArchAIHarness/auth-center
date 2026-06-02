package top.cloudlab.auth.infrastructure.dto;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一响应体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class R<T> {

    /**
     * 业务错误码
     */
    private Integer code;

    /**
     * 响应消息
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String message;

    /**
     * 响应数据
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    /**
     * 是否成功
     * 
     * @return
     */
    @JsonGetter
    public Boolean getSuccess() {
        return 0 == code;
    }
}
