package top.cloudlab.auth.application.service;

import top.cloudlab.auth.application.dto.command.AbstractAccessTokenRequest;
import top.cloudlab.auth.application.dto.response.AccessTokenResponse;

/**
 * 访问凭证服务接口
 * <p>
 * 提供访问令牌的生成功能，支持 AK/SK 和授权码等模式。
 *
 * @param <T> 令牌请求类型
 * @see AccessTokenResponse
 */
public interface TokenAppService<T extends AbstractAccessTokenRequest> {

    /**
     * 生成访问凭证
     *
     * @param command 令牌请求参数
     * @return 访问令牌响应
     */
    AccessTokenResponse generate(T command);

}
