package br.com.student.portal.entity;

import br.com.student.portal.entity.base.BaseEntity;
import br.com.student.portal.entity.enums.EnrollmentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "enrollments", indexes = {
        @Index(name = "idx_enrollments_student_id", columnList = "student_id"),
        @Index(name = "idx_enrollments_course_id", columnList = "course_id"),
        @Index(name = "idx_enrollments_status", columnList = "status")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uk_enrollments_student_course", columnNames = {"student_id", "course_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Enrollment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Builder.Default
    @Column(name = "enrollment_date", nullable = false)
    private LocalDate enrollmentDate = LocalDate.now();

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EnrollmentStatus status = EnrollmentStatus.ACTIVE;

    @Column(precision = 4, scale = 2)
    private BigDecimal grade;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    // ==================== Utility Methods ====================

    public boolean isActive() {
        return status == EnrollmentStatus.ACTIVE;
    }

    public void complete(BigDecimal finalGrade) {
        this.grade = finalGrade;
        this.status = EnrollmentStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
    }

    public void drop() {
        this.status = EnrollmentStatus.DROPPED;
    }

    public void suspend() {
        this.status = EnrollmentStatus.SUSPENDED;
    }

    public void reactivate() {
        this.status = EnrollmentStatus.ACTIVE;
    }
}