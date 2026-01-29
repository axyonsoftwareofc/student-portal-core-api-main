package br.com.student.portal.dto.response;

import br.com.student.portal.entity.enums.EnrollmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentResponse {

    private UUID id;
    private UUID studentId;
    private String studentName;
    private UUID courseId;
    private String courseName;
    private LocalDate enrollmentDate;
    private EnrollmentStatus status;
    private String statusDisplayName;
    private BigDecimal grade;
    private LocalDateTime completedAt;
    private LocalDateTime createdAt;
}