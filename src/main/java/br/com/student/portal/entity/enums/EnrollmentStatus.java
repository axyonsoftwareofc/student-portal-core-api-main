package br.com.student.portal.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EnrollmentStatus {
    ACTIVE("Ativa", "Matrícula ativa"),
    COMPLETED("Concluída", "Curso concluído"),
    DROPPED("Cancelada", "Matrícula cancelada pelo aluno"),
    SUSPENDED("Suspensa", "Matrícula temporariamente suspensa");

    private final String displayName;
    private final String description;

    public boolean isActive() {
        return this == ACTIVE;
    }

    public boolean allowsAccess() {
        return this == ACTIVE;
    }
}