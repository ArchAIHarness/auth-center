package top.cloudlab.auth.infrastructure.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import top.cloudlab.auth.common.annotation.Cache;
import top.cloudlab.auth.domain.user.AccessSecret;
import top.cloudlab.auth.domain.user.TenantPermissions;
import top.cloudlab.auth.domain.user.UserDomainService;
import top.cloudlab.auth.domain.user.UserInfo;
import top.cloudlab.auth.common.dto.R;
import top.cloudlab.auth.infrastructure.dto.request.AccessKeyRequest;
import top.cloudlab.auth.infrastructure.dto.response.AccessKeyResponse;
import top.cloudlab.auth.infrastructure.dto.response.TenantRoleResponse;
import top.cloudlab.auth.infrastructure.dto.response.UserInfoResponse;
import top.cloudlab.auth.infrastructure.feign.client.LabFeignClient;
import top.cloudlab.auth.infrastructure.feign.client.UserFeignClient;

@Slf4j
@Service
public class UserDomainServiceImpl implements UserDomainService {

    private final UserFeignClient userFeignClient;
    private final LabFeignClient labFeignClient;

    public UserDomainServiceImpl(UserFeignClient userFeignClient, LabFeignClient labFeignClient) {
        this.userFeignClient = userFeignClient;
        this.labFeignClient = labFeignClient;
    }

    @Override
    public Optional<AccessSecret> validate(String id, String secret) {
        R<AccessKeyResponse> r = userFeignClient.validate(AccessKeyRequest.builder()
                .id(id)
                .secret(secret)
                .build());
        if (!r.getSuccess()) {
            throw new IllegalArgumentException("invalid access key or secret");
        }
        AccessKeyResponse data = r.getData();
        return Optional.of(AccessSecret.of(
            data.getUserId(),
            data.getAk(),
            data.getSk(),
            data.getScopes()
        ));
    }

    @Override
    public Optional<UserInfo> getUserInfo(String userId) {
        log.info("getUserInfo called, userId: {}", userId);
        UserInfoResponse data = labFeignClient.getUserInfo(userId, "1");
        log.info("labFeignClient.getUserInfo returned: {}", data);
        if (data == null) {
            log.warn("UserInfoResponse is null for userId: {}", userId);
            throw new IllegalArgumentException("invalid user id");
        }
        return Optional.of(UserInfo.of(
            data.getUserId(),
            data.getNickname(),
            data.getAvatar(),
            data.getPhone()
        ));
    }

    @Override
    public Optional<AccessSecret> accessSecret(String accessId) {
        R<AccessKeyResponse> r = userFeignClient.accessSecret(accessId);
        if (!r.getSuccess()) {
            throw new IllegalArgumentException("invalid access id");
        }
        AccessKeyResponse data = r.getData();
        return Optional.of(AccessSecret.of(
            data.getUserId(),
            data.getAk(),
            data.getSk(),
            data.getScopes()
        ));
    }

    @Override
    @Cache(value = "auth:tenant", expire = 300)
    public TenantPermissions getTenantPermissions(String userId) {
        log.info("getTenantPermissions called, userId: {}", userId);
        R<List<TenantRoleResponse>> r = userFeignClient.getTenantRoles(userId);
        if (!r.getSuccess() || r.getData() == null || r.getData().isEmpty()) {
            log.warn("Failed to get tenant permissions for userId: {}", userId);
            return TenantPermissions.builder()
                    .tenants(new java.util.HashSet<>())
                    .permissions(new HashMap<>())
                    .build();
        }

        List<TenantRoleResponse> tenantRoles = r.getData();
        Set<String> tenants = tenantRoles.stream()
                .map(TenantRoleResponse::getTenantId)
                .collect(Collectors.toSet());

        Map<String, List<String>> permissions = new HashMap<>();
        for (TenantRoleResponse tr : tenantRoles) {
            List<String> scopes = new ArrayList<>();
            if (tr.getRoles() != null) {
                for (TenantRoleResponse.RoleInfo role : tr.getRoles()) {
                    if (role.getScopes() != null) {
                        scopes.addAll(role.getScopes());
                    }
                }
            }
            permissions.put(tr.getTenantId(), scopes);
        }

        return TenantPermissions.builder()
                .tenants(tenants)
                .permissions(permissions)
                .build();
    }

}