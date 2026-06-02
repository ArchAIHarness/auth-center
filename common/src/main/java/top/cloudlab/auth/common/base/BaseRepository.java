package top.cloudlab.auth.common.base;

import java.util.Optional;

/**
 * 基础仓储接口
 * 所有 Repository 接口继承此接口
 */
public interface BaseRepository<T, ID> {

    T save(T entity);

    Optional<T> findById(ID id);

    void deleteById(ID id);
}
