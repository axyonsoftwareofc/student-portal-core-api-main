package br.com.student.portal.entity;

import br.com.student.portal.entity.base.BaseEntity;
import br.com.student.portal.entity.enums.SubmissionStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "task_submissions", indexes = {
        @Index(name = "idx_task_submissions_task_id", columnList = "task_id"),
        @Index(name = "idx_task_submissions_student_id", columnList = "student_id"),
        @Index(name = "idx_task_submissions_status", columnList = "status")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uk_task_submissions_task_student", columnNames = {"task_id", "student_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskSubmission extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "file_url", length = 500)
    private String fileUrl;

    @Builder.Default
    @Column(name = "submitted_at", nullable = false)
    private LocalDateTime submittedAt = LocalDateTime.now();

    @Column(precision = 4, scale = 2)
    private BigDecimal grade;

    @Column(columnDefinition = "TEXT")
    private String feedback;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "graded_by")
    private User gradedBy;

    @Column(name = "graded_at")
    private LocalDateTime gradedAt;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SubmissionStatus status = SubmissionStatus.SUBMITTED;

    // ==================== Utility Methods ====================

    public boolean isGraded() {
        return status == SubmissionStatus.GRADED && grade != null;
    }

    public void assignGrade(BigDecimal grade, String feedback, User grader) {
        this.grade = grade;
        this.feedback = feedback;
        this.gradedBy = grader;
        this.gradedAt = LocalDateTime.now();
        this.status = SubmissionStatus.GRADED;
    }

    public void returnForRevision(String feedback, User grader) {
        this.feedback = feedback;
        this.gradedBy = grader;
        this.gradedAt = LocalDateTime.now();
        this.status = SubmissionStatus.RETURNED;
    }

    public boolean isLate() {
        return task != null &&
                task.getDeadline() != null &&
                submittedAt.isAfter(task.getDeadline());
    }
}