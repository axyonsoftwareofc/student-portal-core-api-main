package br.com.student.portal.controller;

import br.com.student.portal.dto.request.PaymentRequest;
import br.com.student.portal.dto.response.PaymentResponse;
import br.com.student.portal.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Pagamentos", description = "Gerenciamento de pagamentos")
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Lista todos os pagamentos")
    public ResponseEntity<List<PaymentResponse>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca pagamento por ID")
    public ResponseEntity<PaymentResponse> getPaymentById(@PathVariable UUID id) {
        return ResponseEntity.ok(paymentService.getPaymentById(id));
    }

    @GetMapping("/student/{studentId}")
    @Operation(summary = "Lista pagamentos do estudante")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByStudentId(@PathVariable UUID studentId) {
        return ResponseEntity.ok(paymentService.getPaymentsByStudentId(studentId));
    }

    @GetMapping("/student/{studentId}/pending")
    @Operation(summary = "Lista pagamentos pendentes do estudante")
    public ResponseEntity<List<PaymentResponse>> getPendingPayments(@PathVariable UUID studentId) {
        return ResponseEntity.ok(paymentService.getPendingPaymentsByStudent(studentId));
    }

    @GetMapping("/student/{studentId}/has-pending")
    @Operation(summary = "Verifica se estudante tem pagamentos pendentes")
    public ResponseEntity<Boolean> hasStudentPendingPayments(@PathVariable UUID studentId) {
        return ResponseEntity.ok(paymentService.hasStudentPendingPayments(studentId));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cria um novo pagamento")
    public ResponseEntity<PaymentResponse> createPayment(@Valid @RequestBody PaymentRequest request) {
        PaymentResponse response = paymentService.createPayment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}/pay")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Marca pagamento como pago")
    public ResponseEntity<PaymentResponse> markAsPaid(@PathVariable UUID id) {
        return ResponseEntity.ok(paymentService.markAsPaid(id));
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cancela um pagamento")
    public ResponseEntity<PaymentResponse> cancelPayment(@PathVariable UUID id) {
        return ResponseEntity.ok(paymentService.cancelPayment(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Remove um pagamento")
    public ResponseEntity<Void> deletePayment(@PathVariable UUID id) {
        paymentService.deletePayment(id);
        return ResponseEntity.noContent().build();
    }
}