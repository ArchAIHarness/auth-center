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
import top.cloudlab.auth.domain.oauth.CodeStatus;
import top.cloudlab.auth.infrastructure.jpa.converter.CodeStatusConverter;
import top.cloudlab.auth.infrastructure.jpa.entity.BaseEntity;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "t_auth_code")
@DynamicInsert
@DynamicUpdate
@SQLRestriction("deleted = 0")
public class AuthCodeEntity extends BaseEntity {

    @Column(name = "code")
    private String code;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "client_id")
    private String clientId;

    @Column(name = "auth_create_time")
    private LocalDateTime authCreateTime;

    @Column(name = "expire_in_seconds")
    private Integer expireInSeconds;

    @Convert(converter = CodeStatusConverter.class)
    @Column(name = "status")
    private CodeStatus status;
}