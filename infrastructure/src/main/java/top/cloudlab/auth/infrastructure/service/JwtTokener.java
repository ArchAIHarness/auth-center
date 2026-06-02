package top.cloudlab.auth.infrastructure.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import top.cloudlab.auth.domain.access.TokenClaims;
import top.cloudlab.auth.domain.access.Tokener;

@Service
public class JwtTokener implements Tokener {

    @Override
    public String generate(TokenClaims claims, String secret) {
        return JWT.create()
                .withJWTId(claims.getId())
                .withSubject(claims.getSubject())
                .withIssuer(claims.getIssuer())
                .withIssuedAt(claims.getIssuedAt() == null ? new Date().toInstant() : claims.getIssuedAt())
                .withExpiresAt(claims.getExpiresAt() == null ? null : claims.getExpiresAt())
                .withPayload(claims.getPayload())
                .sign(Algorithm.HMAC512(secret));
    }

    @Override
    public TokenClaims parse(String token) {
        DecodedJWT decoded = JWT.decode(token);
        Map<String, Object> claims = new HashMap<>();
        decoded.getClaims().forEach((key, value) -> claims.put(key, value.as(Object.class)));
        return TokenClaims.of(
            decoded.getId(),
            decoded.getSubject(),
            decoded.getIssuer(),
            decoded.getIssuedAt() == null ? null
                    : LocalDateTime.ofInstant(decoded.getIssuedAt().toInstant(), ZoneId.systemDefault()),
            decoded.getExpiresAt() == null || decoded.getIssuedAt() == null ? null
                    : decoded.getExpiresAt().toInstant().getEpochSecond()
                            - decoded.getIssuedAt().toInstant().getEpochSecond(),
            claims
        );
    }

    @Override
    public TokenClaims validate(String token, String secret) {
        TokenClaims claims = parse(token);
        Algorithm algorithm = Algorithm.HMAC512(secret);
        JWTVerifier verifier = JWT.require(algorithm).build();
        verifier.verify(token);
        return claims;
    }

}
