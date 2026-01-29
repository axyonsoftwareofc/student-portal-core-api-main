package br.com.student.portal.repository;

import br.com.student.portal.entity.Payment;
import br.com.student.portal.entity.enums.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    // ✅ Agora usa o relacionamento student.id
    @Query("SELECT p FROM Payment p WHERE p.student.id = :studentId ORDER BY p.createdAt DESC")
    List<Payment> findByStudentId(@Param("studentId") UUID studentId);

    List<Payment> findByStatus(PaymentStatus status);

    @Query("SELECT p FROM Payment p WHERE p.dueDate < :date AND p.status = :status")
    List<Payment> findOverduePayments(
            @Param("date") LocalDate date,
            @Param("status") PaymentStatus status
    );

    @Query("SELECT p FROM Payment p WHERE p.student.id = :studentId AND p.status = :status")
    List<Payment> findByStudentIdAndStatus(
            @Param("studentId") UUID studentId,
            @Param("status") PaymentStatus status
    );

    @Query("SELECT p FROM Payment p WHERE p.dueDate BETWEEN :startDate AND :endDate ORDER BY p.dueDate")
    List<Payment> findByDueDateBetween(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Modifying
    @Query("UPDATE Payment p SET p.status = :newStatus, p.updatedAt = CURRENT_TIMESTAMP " +
            "WHERE p.dueDate < :date AND p.status = :currentStatus")
    int updateOverduePayments(
            @Param("date") LocalDate date,
            @Param("currentStatus") PaymentStatus currentStatus,
            @Param("newStatus") PaymentStatus newStatus
    );

    @Query("SELECT COUNT(p) FROM Payment p WHERE p.student.id = :studentId AND p.status = :status")
    long countByStudentIdAndStatus(
            @Param("studentId") UUID studentId,
            @Param("status") PaymentStatus status
    );

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Payment p " +
            "WHERE p.student.id = :studentId AND p.status = :status")
    boolean existsByStudentIdAndStatus(
            @Param("studentId") UUID studentId,
            @Param("status") PaymentStatus status
    );

    Page<Payment> findAllByOrderByCreatedAtDesc(Pageable pageable);

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.student.id = :studentId AND p.status = 'PAGO'")
    java.math.BigDecimal getTotalPaidByStudent(@Param("studentId") UUID studentId);

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.student.id = :studentId AND p.status IN ('PENDENTE', 'ATRASADO')")
    java.math.BigDecimal getTotalPendingByStudent(@Param("studentId") UUID studentId);
}