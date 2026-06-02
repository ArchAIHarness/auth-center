package top.cloudlab.auth.domain.user;

import java.util.Optional;

/**
 * 用户领域服务
 */
public interface UserDomainService {

    /**
     * 校验客户端ID和密钥，通过返回 ClientSecret
     * 
     * @param id
     * @param secret
     * @return
     */
    Optional<AccessSecret> validate(String id, String secret);

    /**
     * 根据用户ID查询用户信息
     * 
     * @param userId
     * @return
     */
    Optional<UserInfo> getUserInfo(String userId);

    /**
     * 获取客户端密钥

     * @param accessId 访问ID
     * @return
     */
    Optional<AccessSecret> accessSecret(String accessId);

    /**
     * 获取用户可访问的租户列表及权限

     * @param userId 用户ID
     * @return 租户权限信息
     */
    TenantPermissions getTenantPermissions(String userId);

}
