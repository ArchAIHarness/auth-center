package top.cloudlab.auth.infrastructure.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccessKeyRequest {

    /**
     * id
     */
    @NotBlank(message = "请输入accessId")
    private String id;

    /**
     * 密钥
     */
    private String secret;

}
