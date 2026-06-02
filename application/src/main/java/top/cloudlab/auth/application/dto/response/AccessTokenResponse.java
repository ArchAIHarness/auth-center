package top.cloudlab.auth.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.cloudlab.auth.domain.access.AccessToken;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccessTokenResponse {

    /**
     * 令牌类型，固定：Bearer
     */
    @Schema(description = "令牌类型")
    private String tokenType;
    /**
     * 访问令牌
     */
    @Schema(description = "访问令牌")
    private String accessToken;
    /**
     * 访问令牌过期时间，单位秒，默认2小时
     */
    @Schema(description = "访问令牌过期时间")
    private Integer tokenExpireInSeconds;
    /**
     * 刷新令牌
     */
    @Schema(description = "刷新令牌")
    private String refreshToken;
    /**
     * 刷新过期时间，单位秒，默认7天
     */
    @Schema(description = "刷新过期时间")
    private Integer refreshExpireInSeconds;

    /**
     * 转化器
     * 
     * @param token
     * @return
     */
    public static AccessTokenResponse convert(AccessToken token) {
        return AccessTokenResponse.builder()
                .tokenType(token.getTokenType().name())
                .accessToken(token.getAccessToken())
                .tokenExpireInSeconds(token.getAccessTokenClaims().getExpireInSeconds().intValue())
                .refreshToken(token.getRefreshToken())
                .refreshExpireInSeconds(token.getRefreshTokenClaims().getExpireInSeconds().intValue())
                .build();
    }

}
