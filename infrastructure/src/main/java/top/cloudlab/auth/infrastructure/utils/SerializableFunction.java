package top.cloudlab.auth.infrastructure.utils;

import java.io.Serializable;
import java.util.function.Function;

/**
 * 可序列化的函数式接口（继承 Function 和 Serializable）
 */
@FunctionalInterface
public interface SerializableFunction<T, O> extends Function<T, O>, Serializable {
    // 继承 Function 的 apply 方法，同时标记为可序列化
}
