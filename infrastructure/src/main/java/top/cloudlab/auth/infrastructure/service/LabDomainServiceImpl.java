package top.cloudlab.auth.infrastructure.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import top.cloudlab.auth.domain.user.LabDomainService;
import top.cloudlab.auth.domain.user.LabUserLogin;
import top.cloudlab.auth.domain.user.LabUserToken;
import top.cloudlab.auth.infrastructure.feign.client.LabFeignClient;

@Service
public class LabDomainServiceImpl implements LabDomainService {

    private final LabFeignClient labFeignClient;

    public LabDomainServiceImpl(LabFeignClient labFeignClient) {
        this.labFeignClient = labFeignClient;
    }

    @Override
    public Optional<LabUserToken> login(LabUserLogin params) {
        return Optional.ofNullable(labFeignClient.ssoLogin(params));
    }
    
}
