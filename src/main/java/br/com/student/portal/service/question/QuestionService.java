package br.com.student.portal.service.question;

import br.com.student.portal.dto.request.QuestionRequest;
import br.com.student.portal.dto.response.QuestionResponse;
import br.com.student.portal.entity.Question;
import br.com.student.portal.entity.User;
import br.com.student.portal.exception.ErrorCode;
import br.com.student.portal.exception.types.ForbiddenException;
import br.com.student.portal.exception.types.NotFoundException;
import br.com.student.portal.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;

    @Transactional(readOnly = true)
    public QuestionResponse getQuestionById(UUID id) {
        log.debug("Buscando pergunta por ID: {}", id);
        Question question = findQuestionOrThrow(id);
        return mapToResponse(question);
    }

    @Transactional(readOnly = true)
    public List<QuestionResponse> getAllQuestions() {
        log.debug("Buscando todas as perguntas");
        return questionRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<QuestionResponse> getAllQuestions(Pageable pageable) {
        log.debug("Buscando perguntas paginadas");
        return questionRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public List<QuestionResponse> searchQuestions(String term) {
        log.debug("Buscando perguntas com termo: {}", term);
        return questionRepository.searchByTerm(term).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public QuestionResponse createQuestion(QuestionRequest request, User author) {
        log.info("Criando nova pergunta: {}", request.getTitle());

        Question question = Question.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .author(author)
                .answerCount(0)
                .build();

        Question savedQuestion = questionRepository.save(question);
        log.info("Pergunta criada com ID: {}", savedQuestion.getId());

        return mapToResponse(savedQuestion);
    }

    @Transactional
    public QuestionResponse updateQuestion(UUID id, QuestionRequest request, User requester) {
        log.info("Atualizando pergunta ID: {}", id);

        Question question = findQuestionOrThrow(id);
        validateAuthorPermission(question, requester);

        question.setTitle(request.getTitle());
        question.setContent(request.getContent());

        Question updatedQuestion = questionRepository.save(question);
        log.info("Pergunta atualizada: {}", updatedQuestion.getId());

        return mapToResponse(updatedQuestion);
    }

    @Transactional
    public void deleteQuestion(UUID id, User requester) {
        log.info("Deletando pergunta ID: {}", id);

        Question question = findQuestionOrThrow(id);

        boolean isAuthor = question.getAuthor().getId().equals(requester.getId());
        boolean isAdmin = requester.isAdmin();

        if (!isAuthor && !isAdmin) {
            throw new ForbiddenException(ErrorCode.FORBIDDEN,
                    "Você não tem permissão para deletar esta pergunta.");
        }

        questionRepository.delete(question);
        log.info("Pergunta deletada: {}", id);
    }

    // ==================== Métodos Privados ====================

    private Question findQuestionOrThrow(UUID id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.QUESTION_NOT_FOUND,
                        "Pergunta não encontrada com ID: " + id));
    }

    private void validateAuthorPermission(Question question, User requester) {
        if (!question.getAuthor().getId().equals(requester.getId())) {
            throw new ForbiddenException(ErrorCode.FORBIDDEN,
                    "Apenas o autor pode editar esta pergunta.");
        }
    }

    private QuestionResponse mapToResponse(Question question) {
        return QuestionResponse.builder()
                .id(question.getId())
                .title(question.getTitle())
                .content(question.getContent())
                .authorId(question.getAuthor().getId())
                .authorName(question.getAuthor().getName())
                .answerCount(question.getAnswerCount() != null ? question.getAnswerCount() : 0)
                .createdAt(question.getCreatedAt())
                .updatedAt(question.getUpdatedAt())
                .build();
    }
}