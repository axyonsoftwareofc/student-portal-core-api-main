package br.com.student.portal.service.auth;

import br.com.student.portal.entity.User;
import br.com.student.portal.exception.ErrorCode;
import br.com.student.portal.exception.types.InternalServerException;
import br.com.student.portal.exception.types.UnauthorizedException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static com.auth0.jwt.algorithms.Algorithm.HMAC256;

@Slf4j
@Service
public class TokenService {

    private static final String ISSUER = "student-portal-api";
    private static final int TOKEN_EXPIRATION_HOURS = 2;

    @Value("${api.security.token}")
    private String secret;

    public String generateToken(User user) {
        try {
            var algorithm = HMAC256(secret);
            return JWT.create()
                    .withIssuer(ISSUER)
                    .withSubject(user.getEmail())
                    .withClaim("userId", user.getId().toString())
                    .withClaim("role", user.getRole().name())
                    .withExpiresAt(LocalDateTime.now()
                            .plusHours(TOKEN_EXPIRATION_HOURS)
                            .toInstant(ZoneOffset.of("-03:00")))
                    .sign(algorithm);

        } catch (JWTCreationException ex) {
            log.error("Erro ao gerar token JWT: {}", ex.getMessage(), ex);
            throw new InternalServerException("Erro ao gerar token de autenticação.", ex);
        }
    }

    public String validateToken(String token) {
        try {
            var algorithm = HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer(ISSUER)
                    .build()
                    .verify(token)
                    .getSubject();

        } catch (JWTVerificationException ex) {
            log.warn("Token inválido ou expirado: {}", ex.getMessage());
            throw new UnauthorizedException(ErrorCode.TOKEN_INVALID, "Token inválido ou expirado.");
        }
    }
}