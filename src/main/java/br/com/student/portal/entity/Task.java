package br.com.student.portal.entity;

import br.com.student.portal.entity.base.BaseEntity;
import br.com.student.portal.entity.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tasks", indexes = {
        @Index(name = "idx_tasks_course_id", columnList = "course_id"),
        @Index(name = "idx_tasks_deadline", columnList = "deadline"),
        @Index(name = "idx_tasks_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private LocalDateTime deadline;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TaskStatus status = TaskStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    public boolean isOverdue() {
        return status == TaskStatus.PENDING &&
                deadline != null &&
                LocalDateTime.now().isAfter(deadline);
    }

    public boolean allowsSubmission() {
        return status != null && status.allowsSubmission();
    }

    public void submit() {
        if (isOverdue()) {
            this.status = TaskStatus.LATE;
        } else {
            this.status = TaskStatus.SUBMITTED;
        }
    }

    public void grade() {
        this.status = TaskStatus.GRADED;
    }

    public void returnForRevision() {
        this.status = TaskStatus.RETURNED;
    }
}