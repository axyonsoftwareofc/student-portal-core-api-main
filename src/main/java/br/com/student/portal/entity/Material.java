package br.com.student.portal.entity;

import br.com.student.portal.entity.base.BaseEntity;
import br.com.student.portal.entity.enums.MaterialCategory;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "materials", indexes = {
        @Index(name = "idx_materials_user_id", columnList = "user_id"),
        @Index(name = "idx_materials_category", columnList = "category"),
        @Index(name = "idx_materials_created_at", columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Material extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MaterialCategory category = MaterialCategory.OTHER;

    @Column(nullable = false)
    private String filename;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "content_type")
    private String contentType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User uploadedBy;

    @Column(name = "upload_date", nullable = false, updatable = false)
    private LocalDateTime uploadDate;

    @Builder.Default
    @Column(nullable = false)
    private Long downloads = 0L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    @Override
    protected void onCreate() {
        super.onCreate();
        this.uploadDate = LocalDateTime.now();
        if (this.downloads == null) this.downloads = 0L;
        if (this.category == null) this.category = MaterialCategory.OTHER;
    }

    // ==================== Utility Methods ====================

    public void incrementDownloads() {
        this.downloads = (this.downloads == null ? 0L : this.downloads) + 1;
    }

    public void autoDetectCategory() {
        if (this.filename != null) {
            this.category = MaterialCategory.fromFilename(this.filename);
        }
    }
}