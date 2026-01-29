package br.com.student.portal.entity;

import br.com.student.portal.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "questions", indexes = {
        @Index(name = "idx_questions_user_id", columnList = "user_id"),
        @Index(name = "idx_questions_created_at", columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Question extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User author;

    @Builder.Default
    @Column(name = "answer_count", nullable = false)
    private Integer answerCount = 0;

    @Builder.Default
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Answer> answers = new ArrayList<>();

    // ==================== Utility Methods ====================

    public void incrementAnswerCount() {
        this.answerCount = (this.answerCount == null ? 0 : this.answerCount) + 1;
    }

    public void decrementAnswerCount() {
        if (this.answerCount != null && this.answerCount > 0) {
            this.answerCount--;
        }
    }

    public boolean hasAnswers() {
        return this.answerCount != null && this.answerCount > 0;
    }
}