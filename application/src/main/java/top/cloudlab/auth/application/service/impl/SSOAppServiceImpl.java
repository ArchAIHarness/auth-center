package top.cloudlab.auth.application.service.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import top.cloudlab.auth.application.dto.command.SSORequest;
import top.cloudlab.auth.application.dto.command.SSOSignatureRequest;
import top.cloudlab.auth.application.dto.command.SSOSignatureRequest.Role;
import top.cloudlab.auth.application.dto.response.SSOResponse;
import top.cloudlab.auth.application.service.SSOAppService;
import top.cloudlab.auth.common.exception.DomainException;
import top.cloudlab.auth.domain.user.AccessSecret;
import top.cloudlab.auth.domain.user.LabDomainService;
import top.cloudlab.auth.domain.user.LabUserLogin;
import top.cloudlab.auth.domain.user.LabUserToken;
import top.cloudlab.auth.domain.user.UserDomainService;

/**
 * SSO 应用服务实现
 * <p>
 * 提供单点登录功能，处理第三方实验室系统的登录请求。
 *
 * @see SSOAppService
 * @see UserDomainService
 * @see LabDomainService
 */
@Slf4j
@Service
public class SSOAppServiceImpl implements SSOAppService {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final UserDomainService userDomainService;
    private final LabDomainService labDomainService;

    public SSOAppServiceImpl(UserDomainService userDomainService, LabDomainService labDomainService) {
        this.userDomainService = userDomainService;
        this.labDomainService = labDomainService;
    }

    /**
     * 处理 SSO 登录请求
     * <p>
     * 验证客户端签名，成功后调用实验室服务获取访问令牌。
     *
     * @param request SSO 登录请求，包含客户端ID和签名参数
     * @return SSO 响应，包含访问令牌和用户信息
     * @throws DomainException 签名验证失败或密钥不存在时抛出
     */
    @Override
    public Optional<SSOResponse> login(SSORequest request) {
        Optional<AccessSecret> optional = userDomainService.accessSecret(request.getClientId());
        if (optional.isEmpty()) {
            throw DomainException.of("INVALID_ACCESS_SECRET", "非法操作，无法执行");
        }
        AccessSecret secret = optional.get();
        String toSignString = request.decodeParamsToString();
        log.info("解码后的签名字符串: {}", toSignString);
        log.debug("客户端传来的 sign: {}", request.getSign());
        log.debug("服务端用 SK 计算的签名: {}", secret.sign(toSignString));
        if (!secret.verify(toSignString, request.getSign())) {
             throw DomainException.of("INVALID_SIGNATURE", "非法操作，无法执行");
        }
        SSOSignatureRequest params = request.decodeParamsToObject();
        try {
            log.info("sso params = {}", objectMapper.writeValueAsString(params));
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            log.warn("SSO参数序列化失败", e);
        }
        Optional<LabUserToken> opt = labDomainService.login(LabUserLogin.of(
                params.getLabCode(),
                params.getUserId(),
                params.getNickname(),
                params.getAvatar(),
                params.getPhone(),
                Optional.ofNullable(params.getRole()).map(Role::getCode).orElse(Role.student.getCode())
        ));
        return opt.map(token -> SSOResponse.builder()
                .token(token.getToken())
                .expire(token.getExpire())
                .userId(token.getUserId())
                .partner(token.getPartner())
                .sign(token.getSign())
                .answerToken(token.getAnswerToken())
                .answerUser(token.getAnswerUser())
                .build());
    }

}
