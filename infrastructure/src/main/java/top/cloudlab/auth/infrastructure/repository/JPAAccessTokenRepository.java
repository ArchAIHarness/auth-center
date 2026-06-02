package top.cloudlab.auth.infrastructure.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import top.cloudlab.auth.infrastructure.entity.AccessTokenEntity;

@Repository
public interface JPAAccessTokenRepository extends JpaRepository<AccessTokenEntity, Long> {
    /**
     * 根据令牌ID查询访问令牌实体
     * 
     * @param tokenId 令牌ID
     * @return 访问令牌实体可选值
     */
    Optional<AccessTokenEntity> findByTokenId(String tokenId);

}
