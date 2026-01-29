package br.com.student.portal.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRole {
    SUPER_USER("Super Usuário", "Acesso total ao sistema", 0),
    ADMIN("Administrador", "Gerencia usuários e conteúdo", 1),
    TEACHER("Professor", "Cria e gerencia materiais e questões", 2),
    STUDENT("Estudante", "Acessa conteúdo e responde questões", 3);

    private final String displayName;
    private final String description;
    private final int hierarchyLevel;

    /**
     * Verifica se este role tem permissão igual ou superior a outro.
     */
    public boolean hasPermissionOver(UserRole other) {
        return this.hierarchyLevel <= other.hierarchyLevel;
    }

    public boolean isAdmin() {
        return this == SUPER_USER || this == ADMIN;
    }

    public boolean isTeacher() {
        return this == TEACHER || isAdmin();
    }

    public boolean isStudent() {
        return this == STUDENT;
    }
}