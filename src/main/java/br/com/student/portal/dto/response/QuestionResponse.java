package br.com.student.portal.dto.response;

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
public class QuestionResponse {

    private UUID id;
    private String title;
    private String content;
    private UUID authorId;
    private String authorName;
    private Integer answerCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}