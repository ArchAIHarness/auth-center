package top.cloudlab.auth.common.base;

public interface ValueObject {

    int getCode();

    @Override
    boolean equals(Object obj);

    @Override
    int hashCode();
}