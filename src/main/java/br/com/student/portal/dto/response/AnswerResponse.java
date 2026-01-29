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
public class AnswerResponse {

    private UUID id;
    private UUID questionId;
    private UUID authorId;
    private String authorName;
    private String content;
    private boolean accepted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}