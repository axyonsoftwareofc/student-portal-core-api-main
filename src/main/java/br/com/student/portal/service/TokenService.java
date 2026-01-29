package br.com.student.portal.service;

import br.com.student.portal.entity.User;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Slf4j
@Service
public class TokenService {

    private static final String ISSUER = "student-portal-api";
    private static final ZoneId ZONE_ID = ZoneId.of("America/Sao_Paulo");

    @Value("${api.security.token.secret}")
    private String secret;

    @Value("${api.security.token.expiration-hours:2}")
    private int expirationHours;

    private Algorithm algorithm;

    @PostConstruct
    private void init() {
        this.algorithm = Algorithm.HMAC256(secret);
        log.info("TokenService inicializado com sucesso");
    }

    public String generateToken(User user) {
        try {
            Instant expiresAt = LocalDateTime.now()
                    .plusHours(expirationHours)
                    .atZone(ZONE_ID)
                    .toInstant();

            String token = JWT.create()
                    .withIssuer(ISSUER)
                    .withSubject(user.getEmail())
                    .withClaim("userId", user.getId().toString())
                    .withClaim("role", user.getRole().name())
                    .withClaim("name", user.getName())
                    .withIssuedAt(Instant.now())
                    .withExpiresAt(expiresAt)
                    .sign(algorithm);

            log.debug("Token gerado para usuário: {}", user.getEmail());
            return token;

        } catch (JWTCreationException ex) {
            log.error("Erro ao gerar token JWT para usuário {}: {}", user.getEmail(), ex.getMessage());
            throw new InternalAuthenticationServiceException("Erro ao gerar token de autenticação.", ex);
        }
    }

    public String validateToken(String token) {
        try {
            DecodedJWT decodedJWT = JWT.require(algorithm)
                    .withIssuer(ISSUER)
                    .build()
                    .verify(token);

            String email = decodedJWT.getSubject();
            log.debug("Token válido para usuário: {}", email);
            return email;

        } catch (JWTVerificationException ex) {
            log.debug("Token inválido ou expirado: {}", ex.getMessage());
            return null;
        }
    }
}