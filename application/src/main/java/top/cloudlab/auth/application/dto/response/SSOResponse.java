package top.cloudlab.auth.application.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SSOResponse {
    private String token;
    private String expire;
    private String userId;
    private String partner;
    private String sign;
    private String answerToken;
    private String answerUser;
}
