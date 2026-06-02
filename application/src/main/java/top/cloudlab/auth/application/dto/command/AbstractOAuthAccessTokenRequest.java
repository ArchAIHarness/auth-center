package top.cloudlab.auth.application.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import top.cloudlab.auth.domain.oauth.GrantType;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * 授权访问凭证请求
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "grantType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = CreateOAuthTokenRequest.class, name = "authorization_code"),
        @JsonSubTypes.Type(value = RefreshOAuthTokenRequest.class, name = "refresh_token")
})
@Getter
@Setter
public abstract class AbstractOAuthAccessTokenRequest implements AbstractAccessTokenRequest {

    @Schema(description = "第三方ID")
    @NotBlank(message = "client_id 不能为空")
    private String clientId;

    @Schema(description = "第三方密钥")
    @NotBlank(message = "secret 不能为空")
    private String secret;

    @Schema(description = "授权类型")
    @NotBlank(message = "grant_type 必须是 authorization_code 或 refresh_token")
    private GrantType grantType;

}
