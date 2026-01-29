package br.com.student.portal.entity;

import br.com.student.portal.entity.base.BaseEntity;
import br.com.student.portal.entity.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_users_email", columnList = "email"),
        @Index(name = "idx_users_registration", columnList = "registration"),
        @Index(name = "idx_users_role", columnList = "role")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole role;

    @Column(unique = true)
    private String registration;

    @Builder.Default
    @Column(name = "access_enable", nullable = false)
    private Boolean accessEnable = true;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return switch (role) {
            case SUPER_USER -> List.of(
                    new SimpleGrantedAuthority("ROLE_SUPER_USER"),
                    new SimpleGrantedAuthority("ROLE_ADMIN"),
                    new SimpleGrantedAuthority("ROLE_TEACHER"),
                    new SimpleGrantedAuthority("ROLE_USER")
            );
            case ADMIN -> List.of(
                    new SimpleGrantedAuthority("ROLE_ADMIN"),
                    new SimpleGrantedAuthority("ROLE_USER")
            );
            case TEACHER -> List.of(
                    new SimpleGrantedAuthority("ROLE_TEACHER"),
                    new SimpleGrantedAuthority("ROLE_USER")
            );
            case STUDENT -> List.of(
                    new SimpleGrantedAuthority("ROLE_STUDENT"),
                    new SimpleGrantedAuthority("ROLE_USER")
            );
        };
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return Boolean.TRUE.equals(accessEnable);
    }

    public boolean isAdmin() {
        return role != null && role.isAdmin();
    }

    public boolean isTeacher() {
        return role == UserRole.TEACHER;
    }

    public boolean isStudent() {
        return role == UserRole.STUDENT;
    }

    public boolean hasAccess() {
        return Boolean.TRUE.equals(accessEnable);
    }
}