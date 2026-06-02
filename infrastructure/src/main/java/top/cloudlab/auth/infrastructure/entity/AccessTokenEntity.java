package top.cloudlab.auth.infrastructure.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import top.cloudlab.auth.domain.access.AuthType;
import top.cloudlab.auth.domain.access.TokenStatus;
import top.cloudlab.auth.domain.access.TokenType;
import top.cloudlab.auth.infrastructure.jpa.converter.AuthTypeConverter;
import top.cloudlab.auth.infrastructure.jpa.converter.TokenStatusConverter;
import top.cloudlab.auth.infrastructure.jpa.converter.TokenTypeConverter;
import top.cloudlab.auth.infrastructure.jpa.entity.BaseEntity;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "t_access_token")
@DynamicInsert
@DynamicUpdate
@SQLRestriction("deleted = 0")
public class AccessTokenEntity extends BaseEntity {

    @Column(name = "token_id", unique = true)
    private String tokenId;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "issuer_id")
    private String issuerId;

    @Convert(converter = TokenTypeConverter.class)
    @Column(name = "token_type")
    private TokenType tokenType;

    @Convert(converter = AuthTypeConverter.class)
    @Column(name = "auth_type")
    private AuthType authType;

    @Column(name = "scopes")
    private String scopes;

    @Column(name = "secret")
    private String secret;

    @Column(name = "token_create_time")
    private LocalDateTime tokenCreateTime;

    @Column(name = "token_expire_in_seconds")
    private Long tokenExpireInSeconds;

    @Column(name = "refresh_expire_in_seconds")
    private Long refreshExpireInSeconds;

    @Convert(converter = TokenStatusConverter.class)
    @Column(name = "status")
    private TokenStatus status;

}