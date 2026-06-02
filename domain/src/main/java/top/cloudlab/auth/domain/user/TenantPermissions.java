package top.cloudlab.auth.domain.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantPermissions {

    private Set<String> tenants;
    private Map<String, List<String>> permissions;

    public Set<String> tenantSet() {
        return tenants != null ? tenants : new HashSet<>();
    }

    public List<String> permissionsOf(String tenant) {
        return permissions != null ? permissions.getOrDefault(tenant, new ArrayList<>()) : new ArrayList<>();
    }

}
