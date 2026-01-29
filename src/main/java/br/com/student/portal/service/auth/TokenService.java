package br.com.student.portal.service.auth;

import br.com.student.portal.entity.User;
import br.com.student.portal.exception.ErrorCode;
import br.com.student.portal.exception.types.InternalServerException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    /**
     * Gera um token JWT para o usuário autenticado.
     *
     * @param user Usuário autenticado
     * @return Token JWT assinado
     */
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
            throw new InternalServerException("Erro ao gerar token de autenticação.", ex);
        }
    }

    /**
     * Valida o token e retorna o email (subject) do usuário.
     *
     * @param token Token JWT
     * @return Email do usuário ou null se inválido
     */
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
            return null; // ✅ Retorna null ao invés de lançar exceção
        }
    }

    /**
     * Extrai informações do token sem validar a assinatura.
     * Útil para logging e debugging.
     *
     * @param token Token JWT
     * @return DecodedJWT ou null se mal formatado
     */
    public DecodedJWT decodeToken(String token) {
        try {
            return JWT.decode(token);
        } catch (Exception ex) {
            log.debug("Erro ao decodificar token: {}", ex.getMessage());
            return null;
        }
    }

    /**
     * Verifica se o token está próximo de expirar (últimos 30 minutos).
     *
     * @param token Token JWT
     * @return true se deve ser renovado
     */
    public boolean shouldRefreshToken(String token) {
        try {
            DecodedJWT decoded = JWT.require(algorithm)
                    .withIssuer(ISSUER)
                    .build()
                    .verify(token);

            Instant expiresAt = decoded.getExpiresAtAsInstant();
            Instant refreshThreshold = Instant.now().plusSeconds(30 * 60); // 30 minutos

            return expiresAt.isBefore(refreshThreshold);

        } catch (JWTVerificationException ex) {
            return false;
        }
    }
}