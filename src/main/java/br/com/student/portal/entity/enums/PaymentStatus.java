package br.com.student.portal.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentStatus {
    PENDENTE("Pendente", "Aguardando pagamento"),
    PAGO("Pago", "Pagamento confirmado"),
    ATRASADO("Atrasado", "Pagamento em atraso"),
    CANCELADO("Cancelado", "Pagamento cancelado"),
    REEMBOLSADO("Reembolsado", "Pagamento reembolsado");

    private final String displayName;
    private final String description;

    public boolean isPending() {
        return this == PENDENTE;
    }

    public boolean isCompleted() {
        return this == PAGO;
    }

    public boolean isOverdue() {
        return this == ATRASADO;
    }

    public boolean allowsModification() {
        return this == PENDENTE || this == ATRASADO;
    }
}