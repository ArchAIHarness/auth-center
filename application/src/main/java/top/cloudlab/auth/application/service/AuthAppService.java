package top.cloudlab.auth.application.service;

import top.cloudlab.auth.application.dto.response.AccessValidatedResponse;
import top.cloudlab.auth.application.dto.response.UserInfoResponse;

/**
 * 认证应用服务接口
 * <p>
 * 提供令牌验证和用户信息查询功能。
 *
 * @see AccessValidatedResponse
 * @see UserInfoResponse
 */
public interface AuthAppService {

    /**
     * 验证访问令牌
     *
     * @param token Bearer 令牌字符串
     * @return 验证结果响应
     */
    AccessValidatedResponse verify(String token);

    /**
     * 获取用户身份信息
     *
     * @param userId 用户ID
     * @return 用户信息响应
     */
    UserInfoResponse userinfo(String userId);

}