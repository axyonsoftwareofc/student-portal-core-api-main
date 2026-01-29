package br.com.student.portal.dto.response;

import br.com.student.portal.entity.enums.CourseStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseResponse {

    private UUID id;
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private CourseStatus status;
    private String statusDisplayName;
    private boolean isActive;
    private boolean isEnrollable;
    private long enrollmentCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}