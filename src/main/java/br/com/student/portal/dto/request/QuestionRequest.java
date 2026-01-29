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
public class QuestionRequest {

    @NotBlank(message = "Título é obrigatório")
    @Size(min = 5, max = 500, message = "Título deve ter entre 5 e 500 caracteres")
    private String title;

    @NotBlank(message = "Conteúdo é obrigatório")
    @Size(min = 10, message = "Conteúdo deve ter no mínimo 10 caracteres")
    private String content;
}