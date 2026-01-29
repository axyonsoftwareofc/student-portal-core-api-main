package br.com.student.portal.dto.response;

import br.com.student.portal.entity.enums.PaymentMethod;
import br.com.student.portal.entity.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private UUID id;
    private UUID studentId;
    private String studentName;
    private BigDecimal amount;
    private LocalDate paymentDate;
    private LocalDate dueDate;
    private PaymentStatus status;
    private String statusDisplayName;
    private PaymentMethod paymentMethod;
    private String transactionId;
    private boolean overdue;
    private LocalDateTime createdAt;
}