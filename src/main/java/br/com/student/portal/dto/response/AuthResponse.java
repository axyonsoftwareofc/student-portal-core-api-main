package br.com.student.portal.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String type;
    private Long expiresIn;
    private UUID userId;
    private String userName;
    private String userEmail;
    private String userRole;

    public static AuthResponse of(String token, UUID userId, String userName, String email, String role) {
        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .expiresIn(7200L)
                .userId(userId)
                .userName(userName)
                .userEmail(email)
                .userRole(role)
                .build();
    }
}