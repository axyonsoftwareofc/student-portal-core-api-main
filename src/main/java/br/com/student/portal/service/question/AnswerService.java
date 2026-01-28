package br.com.student.portal.service.question;

import br.com.student.portal.dto.request.AnswerRequest;
import br.com.student.portal.dto.response.AnswerResponse;
import br.com.student.portal.entity.Answer;
import br.com.student.portal.entity.Question;
import br.com.student.portal.entity.User;
import br.com.student.portal.exception.ErrorCode;
import br.com.student.portal.exception.types.ForbiddenException;
import br.com.student.portal.exception.types.NotFoundException;
import br.com.student.portal.repository.AnswerRepository;
import br.com.student.portal.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AnswerService {

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;

    public AnswerResponse getAnswerById(UUID id) {
        log.debug("Buscando resposta por ID: {}", id);
        Answer answer = findAnswerById(id);
        return mapToResponse(answer);
    }

    @Transactional(readOnly = true)
    public List<AnswerResponse> getAnswersByQuestionId(UUID questionId) {
        log.debug("Buscando respostas da pergunta: {}", questionId);
        return answerRepository.findByQuestionId(questionId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public AnswerResponse createAnswer(AnswerRequest request, UUID questionId, User author) {
        log.info("Criando resposta para pergunta: {}", questionId);

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.QUESTION_NOT_FOUND,
                        "Pergunta não encontrada com ID: " + questionId));

        Answer answer = Answer.builder()
                .content(request.getContent())
                .author(author)
                .question(question)
                .build();

        Answer savedAnswer = answerRepository.save(answer);

        // Incrementar contador de respostas
        question.incrementAnswerCount();
        questionRepository.save(question);

        log.info("Resposta criada com ID: {}", savedAnswer.getId());
        return mapToResponse(savedAnswer);
    }

    public AnswerResponse updateAnswer(UUID id, AnswerRequest request, User requester) {
        log.info("Atualizando resposta ID: {}", id);

        Answer existingAnswer = findAnswerById(id);
        validateAuthorPermission(existingAnswer, requester, "editar");

        existingAnswer.setContent(request.getContent());
        Answer updatedAnswer = answerRepository.save(existingAnswer);

        log.info("Resposta atualizada: {}", updatedAnswer.getId());
        return mapToResponse(updatedAnswer);
    }

    public void deleteAnswer(UUID id, User requester) {
        log.info("Deletando resposta ID: {}", id);

        Answer answer = findAnswerById(id);

        boolean isAuthor = answer.getAuthor().getId().equals(requester.getId());
        boolean isAdmin = requester.isAdmin();

        if (!isAuthor && !isAdmin) {
            throw new ForbiddenException(ErrorCode.FORBIDDEN,
                    "Você não tem permissão para deletar esta resposta.");
        }

        // Decrementar contador de respostas
        Question question = answer.getQuestion();
        question.decrementAnswerCount();
        questionRepository.save(question);

        answerRepository.delete(answer);
        log.info("Resposta deletada: {}", id);
    }

    @Transactional(readOnly = true)
    public long countAnswersByQuestionId(UUID questionId) {
        return answerRepository.countByQuestionId(questionId);
    }

    @Transactional(readOnly = true)
    public List<AnswerResponse> getAnswersByAuthor(UUID authorId) {
        log.debug("Buscando respostas do autor: {}", authorId);
        return answerRepository.findByAuthorId(authorId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ==================== MÉTODOS AUXILIARES ====================

    private Answer findAnswerById(UUID id) {
        return answerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.ANSWER_NOT_FOUND,
                        "Resposta não encontrada com ID: " + id));
    }

    private void validateAuthorPermission(Answer answer, User requester, String action) {
        if (!answer.getAuthor().getId().equals(requester.getId())) {
            throw new ForbiddenException(ErrorCode.FORBIDDEN,
                    "Apenas o autor pode " + action + " esta resposta.");
        }
    }

    private AnswerResponse mapToResponse(Answer entity) {
        return AnswerResponse.builder()
                .id(entity.getId().toString())
                .questionId(entity.getQuestion().getId().toString())
                .authorId(entity.getAuthor().getId().toString())
                .authorName(entity.getAuthor().getName())
                .content(entity.getContent())
                .createdAt(entity.getCreatedAt() != null ? entity.getCreatedAt().toString() : "")
                .updatedAt(entity.getUpdatedAt() != null ? entity.getUpdatedAt().toString() : "")
                .build();
    }
}