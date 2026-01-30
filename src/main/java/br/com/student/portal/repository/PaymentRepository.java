package br.com.student.portal.repository;

import br.com.student.portal.entity.PaymentEntity;
import br.com.student.portal.entity.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity, UUID> {

    @Query("SELECT p FROM Payment p WHERE p.student.id = :studentId ORDER BY p.createdAt DESC")
    List<PaymentEntity> findByStudentId(@Param("studentId") UUID studentId);

    @Query("SELECT p FROM Payment p WHERE p.student.id = :studentId AND p.status = :status")
    List<PaymentEntity> findByStudentIdAndStatus(
            @Param("studentId") UUID studentId,
            @Param("status") PaymentStatus status
    );

    @Modifying
    @Query("UPDATE Payment p SET p.status = :newStatus, p.updatedAt = CURRENT_TIMESTAMP " +
            "WHERE p.dueDate < :date AND p.status = :currentStatus")
    int updateOverduePayments(
            @Param("date") LocalDate date,
            @Param("currentStatus") PaymentStatus currentStatus,
            @Param("newStatus") PaymentStatus newStatus
    );

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Payment p " +
            "WHERE p.student.id = :studentId AND p.status = :status")
    boolean existsByStudentIdAndStatus(
            @Param("studentId") UUID studentId,
            @Param("status") PaymentStatus status
    );
}