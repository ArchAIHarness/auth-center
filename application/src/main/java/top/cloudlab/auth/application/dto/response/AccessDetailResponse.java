package top.cloudlab.auth.application.dto.response;

import java.util.Set;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 访问详情
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccessDetailResponse {

    @Schema(description = "用户ID")
    private String userId;

    @Schema(description = "仅能访问的租户ID列表（为空则不限）")
    private Set<String> scopes;

    @Schema(description = "访问凭证")
    private String accessToken;

    @Schema(description = "刷新凭证")
    private String refreshToken;

}
