package top.cloudlab.auth.infrastructure.repository;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import top.cloudlab.auth.domain.access.AccessToken;
import top.cloudlab.auth.domain.access.AccessTokenId;
import top.cloudlab.auth.domain.access.AccessTokenRepository;
import top.cloudlab.auth.domain.access.Tokener;
import top.cloudlab.auth.infrastructure.entity.AccessTokenEntity;

@Service
public class AccessTokenRepositoryImpl implements AccessTokenRepository {

    private final JPAAccessTokenRepository repository;
    private final Tokener tokener;

    public AccessTokenRepositoryImpl(JPAAccessTokenRepository repository, Tokener tokener) {
        this.repository = repository;
        this.tokener = tokener;
    }

    @Override
    public void save(AccessToken token) {
        AccessTokenEntity entity = repository.findByTokenId(token.getId().value())
                .orElseGet(() -> AccessTokenEntity.builder()
                        .tokenId(token.getId().value())
                        .userId(token.getUserId())
                        .issuerId(token.getIssuerId())
                        .tokenType(token.getTokenType())
                        .authType(token.getAuthType())
                        .scopes(String.join(",", token.getScopes()))
                        .secret(token.getSecret())
                        .tokenCreateTime(token.getCreateTime())
                        .tokenExpireInSeconds(token.getExpireInSeconds())
                        .refreshExpireInSeconds(token.getRefreshExpireInSeconds())
                        .build());
        entity.setStatus(token.getStatus());
        repository.save(entity);
    }

    @Override
    public Optional<AccessToken> findById(AccessTokenId id) {
        Optional<AccessTokenEntity> optional = repository.findByTokenId(id.value());
        if (optional.isEmpty()) {
            return Optional.empty();
        }
        AccessTokenEntity entity = optional.get();
        return Optional.of(AccessToken.reconstruct(
                AccessTokenId.of(entity.getTokenId()),
                tokener,
                entity.getUserId(),
                entity.getIssuerId(),
                entity.getTokenType(),
                entity.getAuthType(),
                entity.getScopes() == null || entity.getScopes().isEmpty() ? Set.of() : Arrays.stream(entity.getScopes().split(",")).collect(Collectors.toSet()),
                entity.getSecret(),
                entity.getTokenCreateTime(),
                entity.getTokenExpireInSeconds(),
                entity.getRefreshExpireInSeconds(),
                entity.getStatus()
        ));
    }

}
