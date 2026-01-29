package br.com.student.portal.dto.response;

import br.com.student.portal.entity.enums.SubmissionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskSubmissionResponse {

    private UUID id;
    private UUID taskId;
    private String taskTitle;
    private UUID studentId;
    private String studentName;
    private String content;
    private String fileUrl;
    private LocalDateTime submittedAt;
    private BigDecimal grade;
    private String feedback;
    private UUID gradedById;
    private String gradedByName;
    private LocalDateTime gradedAt;
    private SubmissionStatus status;
    private String statusDisplayName;
    private boolean late;
}