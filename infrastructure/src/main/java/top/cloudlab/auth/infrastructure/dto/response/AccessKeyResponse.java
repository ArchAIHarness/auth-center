package top.cloudlab.auth.infrastructure.dto.response;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 访问密钥数据传输对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccessKeyResponse {

    /**
     * 用户ID
     */
    private String userId;

    /**
     * id
     */
    @JsonAlias({"accessId", "ak"})
    private String ak;

    /**
     * 密钥
     */
    @JsonAlias({"accessSecret", "sk"})
    private String sk;

    /**
     * 权限范围
     */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<String> scopes;

}
