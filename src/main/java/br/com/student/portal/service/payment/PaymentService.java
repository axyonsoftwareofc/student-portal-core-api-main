package br.com.student.portal.service.payment;

import br.com.student.portal.entity.Payment;
import br.com.student.portal.exception.ErrorCode;
import br.com.student.portal.exception.types.NotFoundException;
import br.com.student.portal.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public List<Payment> getAllPayments() {
        log.debug("Buscando todos os pagamentos");
        return paymentRepository.findAll();
    }

    public Payment getPaymentById(Long id) {
        log.debug("Buscando pagamento por ID: {}", id);
        return paymentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.RESOURCE_NOT_FOUND,
                        "Pagamento não encontrado com ID: " + id));
    }

    public List<Payment> getPaymentsByStudentId(Long studentId) {
        log.debug("Buscando pagamentos do estudante: {}", studentId);
        List<Payment> payments = paymentRepository.findByStudentId(studentId);

        if (payments.isEmpty()) {
            throw new NotFoundException(ErrorCode.RESOURCE_NOT_FOUND,
                    "Nenhum pagamento encontrado para o estudante: " + studentId);
        }

        return payments;
    }

    public Payment createPayment(Payment payment) {
        log.info("Criando novo pagamento para estudante: {}", payment.getStudentId());
        payment.setPaymentDate(LocalDate.now());
        payment.setStatus("PAGO");
        Payment savedPayment = paymentRepository.save(payment);
        log.info("Pagamento criado com ID: {}", savedPayment.getId());
        return savedPayment;
    }

    public void deletePayment(Long id) {
        log.info("Deletando pagamento ID: {}", id);
        Payment payment = getPaymentById(id);
        paymentRepository.delete(payment);
        log.info("Pagamento deletado: {}", id);
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void updateOverduePayments() {
        log.info("Executando atualização de pagamentos atrasados");
        List<Payment> overduePayments = paymentRepository
                .findByDueDateBeforeAndStatus(LocalDate.now(), "PENDENTE");

        overduePayments.forEach(payment -> {
            payment.setStatus("ATRASADO");
            paymentRepository.save(payment);
            log.info("Pagamento {} marcado como ATRASADO", payment.getId());
        });

        log.info("Atualização concluída. {} pagamentos atualizados", overduePayments.size());
    }
}