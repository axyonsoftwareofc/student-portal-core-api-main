package br.com.student.portal.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TaskStatus {
    PENDING("Pendente", "Aguardando envio"),
    SUBMITTED("Enviada", "Tarefa enviada pelo aluno"),
    LATE("Atrasada", "Enviada após o prazo"),
    GRADED("Avaliada", "Tarefa corrigida"),
    RETURNED("Devolvida", "Devolvida para correção");

    private final String displayName;
    private final String description;

    public boolean allowsSubmission() {
        return this == PENDING || this == RETURNED;
    }

    public boolean isCompleted() {
        return this == GRADED;
    }
}