package top.cloudlab.auth.infrastructure.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import top.cloudlab.auth.infrastructure.jpa.entity.BaseEntity;

/**
 * 基础实体 JPA 仓库
 */
@NoRepositoryBean
public interface BaseEntityJpaRepository<T extends BaseEntity> extends JpaRepository<T, Long>,
                JpaSpecificationExecutor<T> {

}
