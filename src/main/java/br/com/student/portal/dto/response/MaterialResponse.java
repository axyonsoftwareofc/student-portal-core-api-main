package br.com.student.portal.dto.response;

import br.com.student.portal.entity.enums.MaterialCategory;
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
public class MaterialResponse {

    private UUID id;
    private String name;
    private String description;
    private String filename;
    private MaterialCategory category;
    private String categoryDisplayName;
    private Long fileSize;
    private String contentType;
    private UUID uploaderId;
    private String uploaderName;
    private UUID courseId;
    private String courseName;
    private Long downloads;
    private LocalDateTime uploadDate;
    private LocalDateTime createdAt;
}