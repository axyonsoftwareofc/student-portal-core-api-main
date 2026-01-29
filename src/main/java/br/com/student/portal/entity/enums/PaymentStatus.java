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

    /**
     * Verifica se o pagamento está em situação pendente.
     */
    public boolean isPending() {
        return this == PENDENTE;
    }

    /**
     * Verifica se o pagamento foi concluído com sucesso.
     */
    public boolean isCompleted() {
        return this == PAGO;
    }

    /**
     * Verifica se o pagamento está em situação irregular.
     */
    public boolean isOverdue() {
        return this == ATRASADO;
    }

    /**
     * Verifica se permite alteração de status.
     */
    public boolean allowsModification() {
        return this == PENDENTE || this == ATRASADO;
    }
}