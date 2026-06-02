package top.cloudlab.auth.application.service;

import java.util.Optional;

import top.cloudlab.auth.application.dto.command.SSORequest;
import top.cloudlab.auth.application.dto.response.SSOResponse;

/**
 * 单点登录应用服务接口
 * <p>
 * 提供 SSO 登录功能，处理第三方系统的单点登录请求。
 *
 * @see SSOResponse
 */
public interface SSOAppService {

    /**
     * 处理 SSO 登录请求
     *
     * @param request SSO 登录请求参数
     * @return SSO 响应，包含访问令牌和用户信息
     */
    Optional<SSOResponse> login(SSORequest request);

}
