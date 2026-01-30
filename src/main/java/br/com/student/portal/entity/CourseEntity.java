package br.com.student.portal.entity;

import br.com.student.portal.entity.base.BaseEntity;
import br.com.student.portal.entity.enums.CourseStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "courses", indexes = {
        @Index(name = "idx_courses_status", columnList = "status"),
        @Index(name = "idx_courses_start_date", columnList = "start_date")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CourseStatus status = CourseStatus.DRAFT;

    @Builder.Default
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TaskEntity> taskEntities = new ArrayList<>();


    public boolean isActive() {
        if (status != CourseStatus.ACTIVE) return false;

        LocalDate today = LocalDate.now();
        boolean started = startDate == null || !today.isBefore(startDate);
        boolean notEnded = endDate == null || !today.isAfter(endDate);
        return started && notEnded;
    }


    public void activate() {
        this.status = CourseStatus.ACTIVE;
    }

    public void complete() {
        this.status = CourseStatus.COMPLETED;
    }

    public void cancel() {
        this.status = CourseStatus.CANCELLED;
    }
}