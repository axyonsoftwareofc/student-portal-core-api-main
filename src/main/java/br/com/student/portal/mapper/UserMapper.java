package br.com.student.portal.mapper;

import br.com.student.portal.dto.request.UserRequest;
import br.com.student.portal.dto.response.UserResponse;
import br.com.student.portal.entity.User;
import br.com.student.portal.entity.enums.UserRole;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(UserRequest request) {
        if (request == null) return null;

        return User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(request.getPassword())
                .registration(request.getRegistration())
                .role(parseRole(request.getRole()))
                .accessEnable(true)
                .build();
    }

    public User userRequestIntoUser(UserRequest request) {
        return toEntity(request);
    }

    public UserResponse toResponse(User user) {
        if (user == null) return null;

        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .roleDisplayName(user.getRole().getDisplayName())
                .registration(user.getRegistration())
                .accessEnable(user.getAccessEnable())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    public UserResponse userIntoUserResponse(User user) {
        return toResponse(user);
    }

    public void updateEntityFromRequest(UserRequest request, User user) {
        if (request == null || user == null) return;

        if (request.getName() != null && !request.getName().isBlank()) {
            user.setName(request.getName());
        }
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            user.setEmail(request.getEmail());
        }
        if (request.getRegistration() != null) {
            user.setRegistration(request.getRegistration());
        }
        if (request.getRole() != null && !request.getRole().isBlank()) {
            user.setRole(parseRole(request.getRole()));
        }
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