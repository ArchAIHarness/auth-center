package top.cloudlab.auth.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccessValidatedResponse {

    @Schema(description = "是否有效")
    private Boolean isValid;

    @Schema(description = "访问详情")
    private AccessDetailResponse access;
}
