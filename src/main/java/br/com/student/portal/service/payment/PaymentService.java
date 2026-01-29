package br.com.student.portal.service.payment;

import br.com.student.portal.dto.request.PaymentRequest;
import br.com.student.portal.dto.response.PaymentResponse;
import br.com.student.portal.entity.Payment;
import br.com.student.portal.entity.User;
import br.com.student.portal.entity.enums.PaymentStatus;
import br.com.student.portal.exception.ErrorCode;
import br.com.student.portal.exception.types.BadRequestException;
import br.com.student.portal.exception.types.NotFoundException;
import br.com.student.portal.repository.PaymentRepository;
import br.com.student.portal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<PaymentResponse> getAllPayments() {
        log.debug("Buscando todos os pagamentos");
        return paymentRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(UUID id) {
        log.debug("Buscando pagamento por ID: {}", id);
        Payment payment = findPaymentOrThrow(id);
        return mapToResponse(payment);
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsByStudentId(UUID studentId) {
        log.debug("Buscando pagamentos do estudante: {}", studentId);
        validateStudentExists(studentId);
        return paymentRepository.findByStudentId(studentId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> getPendingPaymentsByStudent(UUID studentId) {
        log.debug("Buscando pagamentos pendentes do estudante: {}", studentId);
        return paymentRepository.findByStudentIdAndStatus(studentId, PaymentStatus.PENDENTE)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public PaymentResponse createPayment(PaymentRequest request) {
        log.info("Criando novo pagamento para estudante: {}", request.getStudentId());

        User student = userRepository.findById(request.getStudentId())
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND,
                        "Estudante não encontrado com ID: " + request.getStudentId()));

        Payment payment = Payment.builder()
                .student(student)
                .amount(request.getAmount())
                .dueDate(request.getDueDate())
                .paymentMethod(request.getPaymentMethod())
                .status(PaymentStatus.PENDENTE)
                .build();

        Payment savedPayment = paymentRepository.save(payment);
        log.info("Pagamento criado com ID: {}", savedPayment.getId());

        return mapToResponse(savedPayment);
    }

    @Transactional
    public PaymentResponse markAsPaid(UUID id) {
        log.info("Marcando pagamento {} como pago", id);

        Payment payment = findPaymentOrThrow(id);

        if (!payment.allowsModification()) {
            throw new BadRequestException(ErrorCode.OPERATION_NOT_ALLOWED,
                    "Pagamento não pode ser alterado no status atual: " + payment.getStatus());
        }

        payment.markAsPaid();
        Payment updatedPayment = paymentRepository.save(payment);

        log.info("Pagamento {} marcado como PAGO", id);
        return mapToResponse(updatedPayment);
    }

    @Transactional
    public PaymentResponse cancelPayment(UUID id) {
        log.info("Cancelando pagamento {}", id);

        Payment payment = findPaymentOrThrow(id);

        if (!payment.allowsModification()) {
            throw new BadRequestException(ErrorCode.OPERATION_NOT_ALLOWED,
                    "Pagamento não pode ser cancelado no status atual: " + payment.getStatus());
        }

        payment.cancel();
        Payment updatedPayment = paymentRepository.save(payment);

        log.info("Pagamento {} cancelado", id);
        return mapToResponse(updatedPayment);
    }

    @Transactional
    public void deletePayment(UUID id) {
        log.info("Deletando pagamento ID: {}", id);
        Payment payment = findPaymentOrThrow(id);
        paymentRepository.delete(payment);
        log.info("Pagamento deletado: {}", id);
    }

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void updateOverduePayments() {
        log.info("Executando atualização de pagamentos atrasados");

        int updatedCount = paymentRepository.updateOverduePayments(
                LocalDate.now(),
                PaymentStatus.PENDENTE,
                PaymentStatus.ATRASADO
        );

        log.info("Atualização concluída. {} pagamentos marcados como ATRASADO", updatedCount);
    }

    @Transactional(readOnly = true)
    public boolean hasStudentPendingPayments(UUID studentId) {
        return paymentRepository.existsByStudentIdAndStatus(studentId, PaymentStatus.PENDENTE) ||
                paymentRepository.existsByStudentIdAndStatus(studentId, PaymentStatus.ATRASADO);
    }

    // ==================== Métodos Privados ====================

    private Payment findPaymentOrThrow(UUID id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.PAYMENT_NOT_FOUND,
                        "Pagamento não encontrado com ID: " + id));
    }

    private void validateStudentExists(UUID studentId) {
        if (!userRepository.existsById(studentId)) {
            throw new NotFoundException(ErrorCode.USER_NOT_FOUND,
                    "Estudante não encontrado com ID: " + studentId);
        }
    }

    private PaymentResponse mapToResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .studentId(payment.getStudent().getId())
                .studentName(payment.getStudent().getName())
                .amount(payment.getAmount())
                .paymentDate(payment.getPaymentDate())
                .dueDate(payment.getDueDate())
                .status(payment.getStatus())
                .statusDisplayName(payment.getStatus().getDisplayName())
                .paymentMethod(payment.getPaymentMethod())
                .overdue(payment.isOverdue())
                .createdAt(payment.getCreatedAt())
                .build();
    }
}