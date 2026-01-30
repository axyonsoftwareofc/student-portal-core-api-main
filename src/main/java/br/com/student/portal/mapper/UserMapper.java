package br.com.student.portal.mapper;

import br.com.student.portal.dto.request.UserRequest;
import br.com.student.portal.dto.response.UserResponse;
import br.com.student.portal.entity.UserEntity;
import br.com.student.portal.entity.enums.UserRole;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserEntity toEntity(UserRequest request) {
        if (request == null) return null;

        return UserEntity.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(request.getPassword())
                .registration(request.getRegistration())
                .role(parseRole(request.getRole()))
                .accessEnable(true)
                .build();
    }

    public UserEntity userRequestIntoUser(UserRequest request) {
        return toEntity(request);
    }

    public UserResponse toResponse(UserEntity userEntity) {
        if (userEntity == null) return null;

        return UserResponse.builder()
                .id(userEntity.getId())
                .name(userEntity.getName())
                .email(userEntity.getEmail())
                .role(userEntity.getRole().name())
                .roleDisplayName(userEntity.getRole().getDisplayName())
                .registration(userEntity.getRegistration())
                .accessEnable(userEntity.getAccessEnable())
                .createdAt(userEntity.getCreatedAt())
                .updatedAt(userEntity.getUpdatedAt())
                .build();
    }

    public UserResponse userIntoUserResponse(UserEntity userEntity) {
        return toResponse(userEntity);
    }

    private UserRole parseRole(String role) {
        if (role == null || role.isBlank()) {
            return UserRole.STUDENT;
        }
        try {
            return UserRole.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            return UserRole.STUDENT;
        }
    }
}