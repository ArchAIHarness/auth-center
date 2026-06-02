package top.cloudlab.auth.application.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 刷新个人访问凭证请求
 */
@Data
@EqualsAndHashCode(callSuper=false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshAccessTokenRequest extends AbstractOwnerAccessTokenRequest {

    @Schema(description = "刷新凭证")
    private String refreshToken;

}
