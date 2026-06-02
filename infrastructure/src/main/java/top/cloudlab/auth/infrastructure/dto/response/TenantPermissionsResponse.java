package top.cloudlab.auth.infrastructure.dto.response;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantPermissionsResponse {

    @JsonProperty("tenants")
    private List<String> tenants;

    @JsonProperty("permissions")
    private Map<String, List<String>> permissions;

    public Set<String> getTenantSet() {
        return tenants != null ? Set.copyOf(tenants) : Set.of();
    }
}