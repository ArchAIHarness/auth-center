package top.cloudlab.auth.domain.user;

import java.util.Optional;

/**
 * 实验室领域服务
 */
public interface LabDomainService {

    Optional<LabUserToken> login(LabUserLogin params);

}
