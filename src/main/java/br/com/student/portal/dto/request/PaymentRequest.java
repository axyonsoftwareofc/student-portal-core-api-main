package br.com.student.portal.dto.request;

import br.com.student.portal.entity.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {

    @NotNull(message = "ID do estudante é obrigatório")
    private UUID studentId;

    @NotNull(message = "Valor é obrigatório")
    @Positive(message = "Valor deve ser maior que zero")
    private BigDecimal amount;

    @NotNull(message = "Data de vencimento é obrigatória")
    private LocalDate dueDate;

    private PaymentMethod paymentMethod;
}