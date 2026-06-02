package top.cloudlab.auth.application.dto.command;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAccessTokenRequest extends AbstractOwnerAccessTokenRequest {

    @Schema(description = "ak")
    @NotBlank(message = "无效 ak")
    @JsonAlias({ "ak", "id" })
    private String ak;

    @Schema(description = "sk")
    @NotBlank(message = "无效 sk")
    @JsonAlias({ "sk", "secret" })
    private String sk;

    @Schema(description = "租户ID，用于限制只能访问指定的租户")
    @JsonAlias({ "scopes", "scope", "tenantId" })
    private String tenantId;

    public String getAk() {
        return ak;
    }

    public void setAk(String ak) {
        this.ak = ak;
    }

    public String getSk() {
        return sk;
    }

    public void setSk(String sk) {
        this.sk = sk;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
}