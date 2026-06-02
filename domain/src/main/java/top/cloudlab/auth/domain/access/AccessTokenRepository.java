package top.cloudlab.auth.domain.access;

import java.util.Optional;

/**
 * 访问令牌仓库
 */
public interface AccessTokenRepository {

    /**
     * 保存访问令牌
     * 
     * @param token 访问令牌
     */
    void save(AccessToken token);

    /**
     * 根据令牌ID查询访问令牌
     *
     * @param id 访问令牌ID
     * @return 访问令牌
     */
    Optional<AccessToken> findById(AccessTokenId id);

}
