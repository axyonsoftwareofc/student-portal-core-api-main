package br.com.student.portal.dto.response;

import br.com.student.portal.entity.enums.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponse {
    private UUID id;
    private String title;
    private String name;
    private String description;
    private LocalDateTime deadline;
    private TaskStatus status;
    private String statusDisplayName;
    private UUID courseId;
    private String courseName;
    private UUID createdById;
    private String createdByName;
    private boolean overdue;
    private long submissionCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}