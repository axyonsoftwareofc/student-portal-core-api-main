package br.com.student.portal.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CourseStatus {
    DRAFT("Rascunho", "Curso em elaboração"),
    SCHEDULED("Agendado", "Curso agendado para início"),
    ACTIVE("Ativo", "Curso em andamento"),
    PAUSED("Pausado", "Curso temporariamente suspenso"),
    COMPLETED("Concluído", "Curso finalizado"),
    CANCELLED("Cancelado", "Curso cancelado");

    private final String displayName;
    private final String description;

    public boolean isEnrollable() {
        return this == SCHEDULED || this == ACTIVE;
    }

    public boolean isActive() {
        return this == ACTIVE;
    }

    public boolean isFinished() {
        return this == COMPLETED || this == CANCELLED;
    }
}