package top.cloudlab.auth.application.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import top.cloudlab.auth.domain.oauth.GrantType;

/**
 * 创建授权访问凭证
 */
@Data
@EqualsAndHashCode(callSuper=false)
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class CreateOAuthTokenRequest extends AbstractOAuthAccessTokenRequest {

    @Schema(description = "授权码，授权类型：authorization_code 时使用")
    private String code;

    public static class CreateOAuthTokenRequestBuilder {

        private String clientId;
        private String clientSecret;
        private GrantType grantType;

        public CreateOAuthTokenRequestBuilder clientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        public CreateOAuthTokenRequestBuilder clientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
            return this;
        }

        public CreateOAuthTokenRequestBuilder grantType(GrantType grantType) {
            this.grantType = grantType;
            return this;
        }

        public CreateOAuthTokenRequest build() {
            CreateOAuthTokenRequest request = new CreateOAuthTokenRequest(code);
            request.setClientId(clientId);
            request.setSecret(clientSecret);
            request.setGrantType(grantType);
            return request;
        }

    }

    public static CreateOAuthTokenRequestBuilder builder() {
        return new CreateOAuthTokenRequestBuilder();
    }

}
