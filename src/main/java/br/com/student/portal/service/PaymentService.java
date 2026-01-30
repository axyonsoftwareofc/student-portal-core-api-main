package br.com.student.portal.service;

import br.com.student.portal.dto.request.PaymentRequest;
import br.com.student.portal.dto.response.PaymentResponse;
import br.com.student.portal.entity.PaymentEntity;
import br.com.student.portal.entity.UserEntity;
import br.com.student.portal.entity.enums.PaymentStatus;
import br.com.student.portal.exception.BadRequestException;
import br.com.student.portal.exception.ObjectNotFoundException;
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
        PaymentEntity paymentEntity = findPaymentOrThrow(id);
        return mapToResponse(paymentEntity);
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

        UserEntity student = userRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ObjectNotFoundException(
                        "Estudante não encontrado com ID: " + request.getStudentId()));

        //TODO:MOVER ESSA LÓGICA ABAIXO PARA UM BUILDER
        var payment = PaymentEntity.builder()
                .student(student)
                .amount(request.getAmount())
                .dueDate(request.getDueDate())
                .paymentMethod(request.getPaymentMethod())
                .status(PaymentStatus.PENDENTE)
                .build();

        var savedPayment = paymentRepository.save(payment);
        log.info("Pagamento criado com ID: {}", savedPayment.getId());

        return mapToResponse(savedPayment);
    }

    @Transactional
    public PaymentResponse markAsPaid(UUID id) {
        log.info("Marcando pagamento {} como pago", id);

        PaymentEntity paymentEntity = findPaymentOrThrow(id);

        if (!paymentEntity.allowsModification()) {
            throw new BadRequestException(
                    "Pagamento não pode ser alterado no status atual: " + paymentEntity.getStatus());
        }

        paymentEntity.markAsPaid();
        PaymentEntity updatedPaymentEntity = paymentRepository.save(paymentEntity);

        log.info("Pagamento {} marcado como PAGO", id);
        return mapToResponse(updatedPaymentEntity);
    }

    @Transactional
    public PaymentResponse cancelPayment(UUID id) {
        log.info("Cancelando pagamento {}", id);

        var payment = findPaymentOrThrow(id);

        if (!payment.allowsModification()) {
            throw new BadRequestException(
                    "Pagamento não pode ser cancelado no status atual: " + payment.getStatus());
        }

        payment.cancel();
        PaymentEntity updatedPaymentEntity = paymentRepository.save(payment);

        log.info("Pagamento {} cancelado", id);
        return mapToResponse(updatedPaymentEntity);
    }

    @Transactional
    public void deletePayment(UUID id) {
        log.info("Deletando pagamento ID: {}", id);
        var payment = findPaymentOrThrow(id);
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


    private PaymentEntity findPaymentOrThrow(UUID id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException(
                        "Pagamento não encontrado com ID: " + id));
    }

    private void validateStudentExists(UUID studentId) {
        if (!userRepository.existsById(studentId)) {
            throw new ObjectNotFoundException(
                    "Estudante não encontrado com ID: " + studentId);
        }
    }

    //TODO:MOVER ISSO PARA UM MAPPER
    private PaymentResponse mapToResponse(PaymentEntity paymentEntity) {
        return PaymentResponse.builder()
                .id(paymentEntity.getId())
                .studentId(paymentEntity.getStudent().getId())
                .studentName(paymentEntity.getStudent().getName())
                .amount(paymentEntity.getAmount())
                .paymentDate(paymentEntity.getPaymentDate())
                .dueDate(paymentEntity.getDueDate())
                .status(paymentEntity.getStatus())
                .statusDisplayName(paymentEntity.getStatus().getDisplayName())
                .paymentMethod(paymentEntity.getPaymentMethod())
                .overdue(paymentEntity.isOverdue())
                .createdAt(paymentEntity.getCreatedAt())
                .build();
    }
}