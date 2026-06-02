package top.cloudlab.auth.infrastructure.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import top.cloudlab.auth.domain.oauth.AuthCode;
import top.cloudlab.auth.domain.oauth.AuthCodeRepository;
import top.cloudlab.auth.domain.oauth.CodeStatus;
import top.cloudlab.auth.infrastructure.entity.AuthCodeEntity;

@Service
@RequiredArgsConstructor
public class AuthCodeRepositoryImpl implements AuthCodeRepository {

    private final JPAAuthCodeRepository repository;

    @Override
    public void save(AuthCode code) {
        AuthCodeEntity entity = repository
                .findFirstByClientIdAndCodeOrderByIdDesc(code.getClientId(), code.getCode())
                .orElseGet(() -> AuthCodeEntity.builder()
                        .code(code.getCode())
                        .userId(code.getUserId())
                        .clientId(code.getClientId())
                        .authCreateTime(code.getCreateTime())
                        .expireInSeconds(code.getExpireInSeconds().intValue())
                        .build());
        entity.setStatus(code.getStatus());
        repository.save(entity);
    }

    @Override
    public Optional<AuthCode> findFirst(String clientId, String code) {
        Optional<AuthCodeEntity> optional = repository
                .findFirstByClientIdAndCodeOrderByIdDesc(clientId, code);
        if (optional.isEmpty()) {
            return Optional.empty();
        }
        AuthCodeEntity entity = optional.get();
        return Optional.of(AuthCode.reconstruct(
                entity.getCode(),
                entity.getUserId(),
                entity.getClientId(),
                entity.getExpireInSeconds().longValue(),
                entity.getAuthCreateTime(),
                entity.getStatus()));
    }

    @SuppressWarnings("null")
    @Override
    public void revokeAll(String clientId, String userId) {
        List<AuthCodeEntity> entities = repository.findByClientIdAndUserIdAndStatus(clientId, userId, CodeStatus.VALID);
        for (AuthCodeEntity entity : entities) {
            entity.setStatus(CodeStatus.EXPIRED);
        }
        repository.saveAll(entities);
    }

}
