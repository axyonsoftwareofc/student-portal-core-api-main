package br.com.student.portal.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskSubmissionRequest {

    @NotNull(message = "ID da tarefa é obrigatório")
    private UUID taskId;

    private String content;

    private String fileUrl;
}