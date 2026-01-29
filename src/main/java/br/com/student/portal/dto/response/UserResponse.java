package br.com.student.portal.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private UUID id;
    private String name;
    private String email;
    private String role;
    private String roleDisplayName;
    private String registration;
    private Boolean accessEnable;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}