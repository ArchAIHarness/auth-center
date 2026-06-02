package top.cloudlab.auth.domain.oauth;

import java.util.Optional;

/**
 * 授权码
 */
public interface AuthCodeRepository {

    /**
     * 保存
     */
    void save(AuthCode code);

    /**
     * 获取最新的客户端授权码
     * 
     * @param clientId
     * @param code
     * @return
     */
    Optional<AuthCode> findFirst(String clientId, String code);

    /**
     * 撤销所有客户端用户授权码
     * 
     * @param clientId
     * @param userId
     */
    void revokeAll(String clientId, String userId);

}
