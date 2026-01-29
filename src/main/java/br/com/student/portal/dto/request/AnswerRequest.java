package br.com.student.portal.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnswerRequest {

    @NotBlank(message = "Conteúdo é obrigatório")
    @Size(min = 5, message = "Resposta deve ter no mínimo 5 caracteres")
    private String content;
}