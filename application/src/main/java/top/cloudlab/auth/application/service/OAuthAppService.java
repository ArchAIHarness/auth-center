package top.cloudlab.auth.application.service;

import top.cloudlab.auth.application.dto.response.AuthCodeResponse;

/**
 * OAuth 应用服务接口
 * <p>
 * 提供 OAuth 2.0 授权码流程相关功能。
 *
 * @see AuthCodeResponse
 */
public interface OAuthAppService {

    /**
     * 为用户生成授权码
     *
     * @param clientId 客户端ID
     * @param userId   用户ID
     * @return 授权码响应
     */
    AuthCodeResponse authorize(String clientId, String userId);

}
