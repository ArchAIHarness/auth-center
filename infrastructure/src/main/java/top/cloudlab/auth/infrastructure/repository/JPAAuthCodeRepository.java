package top.cloudlab.auth.infrastructure.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import top.cloudlab.auth.domain.oauth.CodeStatus;
import top.cloudlab.auth.infrastructure.entity.AuthCodeEntity;

@Repository
public interface JPAAuthCodeRepository extends JpaRepository<AuthCodeEntity, Long> {

    /**
     * 获取最新的客户端授权码
     * 
     * @param clientId
     * @param code
     * @return
     */
    Optional<AuthCodeEntity> findFirstByClientIdAndCodeOrderByIdDesc(String clientId, String code);

    /**
     * 查询指定状态的客户端用户授权码
     * @param clientId
     * @param userId
     * @param status
     * @return
     */
    List<AuthCodeEntity> findByClientIdAndUserIdAndStatus(String clientId, String userId, CodeStatus status);

}
