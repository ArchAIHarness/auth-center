package top.cloudlab.auth.infrastructure.feign.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import top.cloudlab.auth.common.dto.R;
import top.cloudlab.auth.infrastructure.dto.request.AccessKeyRequest;
import top.cloudlab.auth.infrastructure.dto.response.AccessKeyResponse;
import top.cloudlab.auth.infrastructure.dto.response.TenantRoleResponse;
import top.cloudlab.auth.infrastructure.feign.factory.UserFeignClientFallbackFactory;

import java.util.List;

@FeignClient(name = "user-service", contextId = "userFeignClient", url = "${user-service.url:http://user:80}", fallbackFactory = UserFeignClientFallbackFactory.class)
public interface UserFeignClient {

    @PostMapping(value = "/access/validate")
    public R<AccessKeyResponse> validate(@RequestBody AccessKeyRequest query);

    @GetMapping("/access/access_secret")
    public R<AccessKeyResponse> accessSecret(@RequestHeader("x-access-id") String accessId);

    @GetMapping(value = "/tenant/role")
    public R<List<TenantRoleResponse>> getTenantRoles(@RequestHeader("x-user-id") String userId);

}
