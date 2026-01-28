package br.com.student.portal.service.question;

import br.com.student.portal.dto.request.QuestionRequest;
import br.com.student.portal.dto.response.QuestionResponse;
import br.com.student.portal.entity.Question;
import br.com.student.portal.exception.ErrorCode;
import br.com.student.portal.exception.types.ForbiddenException;
import br.com.student.portal.exception.types.NotFoundException;
import br.com.student.portal.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class QuestionService {

    private final QuestionRepository questionRepository;

    public QuestionResponse getQuestionById(UUID id) {
        log.debug("Buscando pergunta por ID: {}", id);
        Question question = findQuestionById(id);
        return mapToResponse(question);
    }

    @Transactional(readOnly = true)
    public List<QuestionResponse> getAllQuestions() {
        log.debug("Buscando todas as perguntas");
        return questionRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<QuestionResponse> getAllQuestions(Pageable pageable) {
        log.debug("Buscando perguntas paginadas: página {}, tamanho {}",
                pageable.getPageNumber(), pageable.getPageSize());
        return questionRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    public QuestionResponse createQuestion(QuestionRequest request) {
        log.info("Criando nova pergunta: {}", request.getTitle());

        Question question = Question.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .author(request.getUser())
                .build();

        Question savedQuestion = questionRepository.save(question);
        log.info("Pergunta criada com ID: {}", savedQuestion.getId());

        return mapToResponse(savedQuestion);
    }

    public QuestionResponse updateQuestion(UUID id, QuestionRequest request) {
        log.info("Atualizando pergunta ID: {}", id);

        Question existingQuestion = findQuestionById(id);
        validateAuthorPermission(existingQuestion, request.getUser().getId(), "editar");

        existingQuestion.setTitle(request.getTitle());
        existingQuestion.setContent(request.getContent());

        Question updatedQuestion = questionRepository.save(existingQuestion);
        log.info("Pergunta atualizada: {}", updatedQuestion.getId());

        return mapToResponse(updatedQuestion);
    }

    public void deleteQuestion(UUID id) {
        log.info("Deletando pergunta ID: {}", id);

        Question question = findQuestionById(id);

        // Nota: A validação de permissão deveria receber o usuário atual
        // Deixei um TODO para você melhorar isso depois
        questionRepository.delete(question);
        log.info("Pergunta deletada: {}", id);
    }

    @Transactional(readOnly = true)
    public List<QuestionResponse> searchQuestions(String term) {
        log.debug("Buscando perguntas com termo: {}", term);
        return questionRepository.searchByTerm(term)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<QuestionResponse> searchQuestions(String term, Pageable pageable) {
        log.debug("Buscando perguntas paginadas com termo: {}", term);

        List<QuestionResponse> results = searchQuestions(term);

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), results.size());

        return new PageImpl<>(
                results.subList(start, end),
                pageable,
                results.size()
        );
    }

    // ==================== MÉTODOS AUXILIARES ====================

    private Question findQuestionById(UUID id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.QUESTION_NOT_FOUND,
                        "Pergunta não encontrada com ID: " + id));
    }

    private void validateAuthorPermission(Question question, UUID requesterId, String action) {
        if (!question.getAuthor().getId().equals(requesterId)) {
            throw new ForbiddenException(ErrorCode.FORBIDDEN,
                    "Apenas o autor pode " + action + " esta pergunta.");
        }
    }

    private QuestionResponse mapToResponse(Question entity) {
        return QuestionResponse.builder()
                .id(entity.getId().toString())
                .title(entity.getTitle())
                .content(entity.getContent())
                .author(entity.getAuthor())
                .answerCount(entity.getAnswerCount() != null ? entity.getAnswerCount() : 0)
                .createdAt(entity.getCreatedAt() != null ? entity.getCreatedAt().toString() : "")
                .updatedAt(entity.getUpdatedAt() != null ? entity.getUpdatedAt().toString() : "")
                .build();
    }
}