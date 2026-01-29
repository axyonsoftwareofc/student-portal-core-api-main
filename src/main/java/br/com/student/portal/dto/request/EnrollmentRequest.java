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
public class EnrollmentRequest {

    @NotNull(message = "ID do estudante é obrigatório")
    private UUID studentId;

    @NotNull(message = "ID do curso é obrigatório")
    private UUID courseId;
}