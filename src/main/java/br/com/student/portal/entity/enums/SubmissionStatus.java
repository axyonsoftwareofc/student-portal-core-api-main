package br.com.student.portal.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SubmissionStatus {
    SUBMITTED("Enviada", "Aguardando avaliação"),
    GRADED("Avaliada", "Nota atribuída"),
    RETURNED("Devolvida", "Devolvida para correção");

    private final String displayName;
    private final String description;

    public boolean isPending() {
        return this == SUBMITTED;
    }

    public boolean allowsResubmission() {
        return this == RETURNED;
    }
}