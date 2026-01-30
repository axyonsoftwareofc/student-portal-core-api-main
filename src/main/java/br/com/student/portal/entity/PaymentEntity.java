package br.com.student.portal.entity;

import br.com.student.portal.entity.base.BaseEntity;
import br.com.student.portal.entity.enums.PaymentMethod;
import br.com.student.portal.entity.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "payments", indexes = {
        @Index(name = "idx_payments_student_id", columnList = "student_id"),
        @Index(name = "idx_payments_status", columnList = "status"),
        @Index(name = "idx_payments_due_date", columnList = "due_date")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private UserEntity student;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "payment_date")
    private LocalDate paymentDate;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus status = PaymentStatus.PENDENTE;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", length = 20)
    private PaymentMethod paymentMethod;

    @Column(name = "transaction_id")
    private String transactionId;

    @Column(columnDefinition = "TEXT")
    private String notes;

    public boolean isOverdue() {
        return status == PaymentStatus.PENDENTE &&
                dueDate != null &&
                LocalDate.now().isAfter(dueDate);
    }

    public void markAsPaid(PaymentMethod method) {
        this.status = PaymentStatus.PAGO;
        this.paymentDate = LocalDate.now();
        this.paymentMethod = method;
    }

    public void markAsPaid() {
        markAsPaid(this.paymentMethod);
    }

    public void markAsOverdue() {
        this.status = PaymentStatus.ATRASADO;
    }

    public void cancel() {
        this.status = PaymentStatus.CANCELADO;
    }

    public void refund() {
        this.status = PaymentStatus.REEMBOLSADO;
    }

    public boolean allowsModification() {
        return status != null && status.allowsModification();
    }
}