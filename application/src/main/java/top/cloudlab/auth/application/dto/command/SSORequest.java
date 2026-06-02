package top.cloudlab.auth.application.dto.command;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import top.cloudlab.auth.application.dto.command.SSOSignatureRequest.Role;

/**
 * 单点登录请求
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class SSORequest {

    @Schema(description = "客户端ID")
    private String clientId;

    @Schema(description = "加签参数, Base64编码的签名字符串")
    private String params;

    @Schema(description = "签名")
    private String sign;

    @Schema(description = "回调地址")
    private String redirectUri;

    // 兼容 Query 参数中的 client_id
    public void setClient_id(String client_id) {
        this.clientId = client_id;
    }

    // 兼容 Query 参数中的 redirect_uri
    public void setRedirect_uri(String redirect_uri) {
        this.redirectUri = redirect_uri;
    }

    public void setSign(String sign) {
        this.sign = URLDecoder.decode(sign.replace("+", "%2B"), StandardCharsets.UTF_8);
    }

    public String decodeParamsToString() {
        try {
            byte[] decodedBytes = Base64.getUrlDecoder().decode(params);
            return new String(decodedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Failed to decode params", e);
        }
    }

    public SSOSignatureRequest decodeParamsToObject() {
        String queryString = decodeParamsToString();
        SSOSignatureRequest request = new SSOSignatureRequest();
        String[] pairs = queryString.split("&");
        for (String pair : pairs) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2) {
                switch (kv[0]) {
                    case "labCode":
                        request.setLabCode(kv[1]);
                        break;
                    case "userId":
                        request.setUserId(kv[1]);
                        break;
                    case "nickname":
                        request.setNickname(kv[1]);
                        break;
                    case "avatar":
                        request.setAvatar(kv[1]);
                        break;
                    case "phone":
                        request.setPhone(kv[1]);
                        break;
                    case "role":
                        request.setRole(Role.valueOf(kv[1]));
                        break;
                }
            }
        }
        return request;
    }

}
