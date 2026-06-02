package top.cloudlab.auth.infrastructure.feign.factory;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import top.cloudlab.auth.domain.user.LabUserLogin;
import top.cloudlab.auth.domain.user.LabUserToken;
import top.cloudlab.auth.infrastructure.dto.response.UserInfoResponse;
import top.cloudlab.auth.infrastructure.feign.client.LabFeignClient;

@Slf4j
@Component
public class LabFeignClientFallbackFactory implements FallbackFactory<LabFeignClient> {

    @Override
    public LabFeignClient create(Throwable cause) {

        log.error("调用实验室服务失败", cause);
        return new LabFeignClient() {

            @Override
            public UserInfoResponse getUserInfo(String userId, String ptPhone) {
                log.error("调用实验室服务失败: {}", cause.getMessage());
                return null;
            }

            @Override
            public LabUserToken ssoLogin(LabUserLogin labUser) {
                log.error("调用实验室服务失败: {}", cause.getMessage());
                return null;
            }

        };
    }

}
