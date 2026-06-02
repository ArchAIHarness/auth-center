package top.cloudlab.auth.infrastructure.feign.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import top.cloudlab.auth.domain.user.LabUserLogin;
import top.cloudlab.auth.domain.user.LabUserToken;
import top.cloudlab.auth.infrastructure.dto.response.UserInfoResponse;
import top.cloudlab.auth.infrastructure.feign.factory.LabFeignClientFallbackFactory;

@FeignClient(name = "lab-service", contextId = "labFeignClient", url = "${lab-service.url:http://lab}", fallbackFactory = LabFeignClientFallbackFactory.class)
public interface LabFeignClient {

    @GetMapping(value = "/api/v0.3/user/detail/info")
    public UserInfoResponse getUserInfo(@RequestHeader("user-id") String userId,
            @RequestHeader("pt-phone") String ptPhone);

    @PostMapping(value = "/api/v0.3/sso/login")
    public LabUserToken ssoLogin(@RequestBody LabUserLogin labUser);

}
