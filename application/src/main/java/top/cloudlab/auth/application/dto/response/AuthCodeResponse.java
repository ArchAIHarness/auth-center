package top.cloudlab.auth.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.cloudlab.auth.domain.oauth.AuthCode;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthCodeResponse {
    
    @Schema(description = "客户端id")
    private String clientId;

    @Schema(description = "授权码")
    private String code;

    @Schema(description = "过期时间")
    private Integer expireInSeconds;

    public static AuthCodeResponse convert(AuthCode code) {
        return AuthCodeResponse.builder()
                .clientId(code.getClientId())
                .code(code.getCode())
                .expireInSeconds(code.getExpireInSeconds().intValue())
                .build();
    }
}
