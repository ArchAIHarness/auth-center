package top.cloudlab.auth.application.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import top.cloudlab.auth.domain.oauth.GrantType;

/**
 * 刷新授权访问凭证
 */
@Data
@EqualsAndHashCode(callSuper=false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshOAuthTokenRequest extends AbstractOAuthAccessTokenRequest {

    @Schema(description = "刷新凭证，授权类型：refresh_token 时使用")
    private String refreshToken;

    public static class RefreshOAuthTokenRequestBuilder {

        private String clientId;
        private String clientSecret;
        private GrantType grantType;

        public RefreshOAuthTokenRequestBuilder clientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        public RefreshOAuthTokenRequestBuilder clientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
            return this;
        }

        public RefreshOAuthTokenRequestBuilder grantType(GrantType grantType) {
            this.grantType = grantType;
            return this;
        }

        public RefreshOAuthTokenRequest build() {
            RefreshOAuthTokenRequest request = new RefreshOAuthTokenRequest(refreshToken);
            request.setClientId(clientId);
            request.setSecret(clientSecret);
            request.setGrantType(grantType);
            return request;
        }

    }

    public static RefreshOAuthTokenRequestBuilder builder() {
        return new RefreshOAuthTokenRequestBuilder();
    }

}
