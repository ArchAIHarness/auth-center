package top.cloudlab.auth.infrastructure.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantRoleResponse {

    @JsonProperty("tenantId")
    private String tenantId;

    @JsonProperty("tenantUserId")
    private String tenantUserId;

    @JsonProperty("roles")
    private List<RoleInfo> roles;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RoleInfo {
        @JsonProperty("roleId")
        private String roleId;

        @JsonProperty("title")
        private String title;

        @JsonProperty("scopes")
        private List<String> scopes;
    }
}