package br.com.student.portal.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentMethod {
    PIX("PIX", "Pagamento instantâneo"),
    CREDIT_CARD("Cartão de Crédito", "Pagamento com cartão de crédito"),
    DEBIT_CARD("Cartão de Débito", "Pagamento com cartão de débito"),
    BANK_SLIP("Boleto", "Boleto bancário"),
    BANK_TRANSFER("Transferência", "Transferência bancária");

    private final String displayName;
    private final String description;
}