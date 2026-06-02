package top.cloudlab.auth.infrastructure.feign.factory;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import top.cloudlab.auth.common.dto.R;
import top.cloudlab.auth.infrastructure.dto.request.AccessKeyRequest;
import top.cloudlab.auth.infrastructure.dto.response.AccessKeyResponse;
import top.cloudlab.auth.infrastructure.dto.response.TenantRoleResponse;
import top.cloudlab.auth.infrastructure.feign.client.UserFeignClient;

import java.util.List;

@Slf4j
@Component
public class UserFeignClientFallbackFactory implements FallbackFactory<UserFeignClient> {

    @Override
    public UserFeignClient create(Throwable cause) {

        log.error("调用用户服务失败", cause);
        return new UserFeignClient() {

            @Override
            public R<AccessKeyResponse> validate(AccessKeyRequest query) {
                log.error("调用用户服务失败: {}", cause.getMessage());
                return R.<AccessKeyResponse>builder()
                        .code(500)
                        .message("调用用户服务失败")
                        .build();
            }

            @Override
            public R<AccessKeyResponse> accessSecret(String accessId) {
                log.error("调用用户服务失败: {}", cause.getMessage());
                return R.<AccessKeyResponse>builder()
                        .code(500)
                        .message("调用用户服务失败")
                        .build();
            }

            @Override
            public R<List<TenantRoleResponse>> getTenantRoles(String userId) {
                log.error("调用用户服务失败: {}", cause.getMessage());
                return R.<List<TenantRoleResponse>>builder()
                        .code(500)
                        .message("调用用户服务失败")
                        .build();
            }
        };
    }

}