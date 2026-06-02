package top.cloudlab.auth.application.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import top.cloudlab.auth.application.dto.response.AuthCodeResponse;
import top.cloudlab.auth.application.service.OAuthAppService;
import top.cloudlab.auth.domain.oauth.AuthCode;
import top.cloudlab.auth.domain.oauth.AuthCodeRepository;
import top.cloudlab.auth.domain.oauth.CodeGenerator;

/**
 * OAuth 应用服务实现
 * <p>
 * 提供 OAuth 2.0 授权码生成和验证功能。
 *
 * @see OAuthAppService
 */
@Service
public class OAuthAppServiceImpl implements OAuthAppService {

    private final CodeGenerator generator;
    private final AuthCodeRepository authCodeRepository;

    public OAuthAppServiceImpl(CodeGenerator generator, AuthCodeRepository authCodeRepository) {
        this.generator = generator;
        this.authCodeRepository = authCodeRepository;
    }

    /**
     * 为用户生成授权码
     *
     * @param clientId 客户端ID
     * @param userId   用户ID
     * @return 授权码响应
     */
    @Override
    @Transactional
    public AuthCodeResponse authorize(String clientId, String userId) {
        AuthCode code = AuthCode.create(generator, userId, clientId);
        authCodeRepository.revokeAll(clientId, userId);
        authCodeRepository.save(code);
        return AuthCodeResponse.convert(code);
    }

}
