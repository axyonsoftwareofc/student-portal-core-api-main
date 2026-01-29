package br.com.student.portal.entity;

import br.com.student.portal.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "answers", indexes = {
        @Index(name = "idx_answers_user_id", columnList = "user_id"),
        @Index(name = "idx_answers_question_id", columnList = "question_id"),
        @Index(name = "idx_answers_created_at", columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Answer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Builder.Default
    @Column(name = "is_accepted")
    private Boolean accepted = false;

    // ==================== Utility Methods ====================

    public boolean isAccepted() {
        return Boolean.TRUE.equals(accepted);
    }

    public void accept() {
        this.accepted = true;
    }
}