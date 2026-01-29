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

@Slf4j
@Service
@RequiredArgsConstructor
public class AnswerService {

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;

    @Transactional(readOnly = true)
    public AnswerResponse getAnswerById(UUID id) {
        log.debug("Buscando resposta por ID: {}", id);
        Answer answer = findAnswerOrThrow(id);
        return mapToResponse(answer);
    }

    @Transactional(readOnly = true)
    public List<AnswerResponse> getAnswersByQuestionId(UUID questionId) {
        log.debug("Buscando respostas da pergunta: {}", questionId);
        return answerRepository.findByQuestionId(questionId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AnswerResponse> getAnswersByAuthor(UUID authorId) {
        log.debug("Buscando respostas do autor: {}", authorId);
        return answerRepository.findByAuthorId(authorId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public long countAnswersByQuestionId(UUID questionId) {
        return answerRepository.countByQuestionId(questionId);
    }

    @Transactional
    public AnswerResponse createAnswer(AnswerRequest request, UUID questionId, User author) {
        log.info("Criando resposta para pergunta: {}", questionId);

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.QUESTION_NOT_FOUND,
                        "Pergunta não encontrada com ID: " + questionId));

        Answer answer = Answer.builder()
                .content(request.getContent())
                .author(author)
                .question(question)
                .accepted(false)
                .build();

        Answer savedAnswer = answerRepository.save(answer);

        // O trigger no banco atualiza automaticamente o answer_count
        log.info("Resposta criada com ID: {}", savedAnswer.getId());

        return mapToResponse(savedAnswer);
    }

    @Transactional
    public AnswerResponse updateAnswer(UUID id, AnswerRequest request, User requester) {
        log.info("Atualizando resposta ID: {}", id);

        Answer answer = findAnswerOrThrow(id);
        validateAuthorPermission(answer, requester);

        answer.setContent(request.getContent());
        Answer updatedAnswer = answerRepository.save(answer);

        log.info("Resposta atualizada: {}", updatedAnswer.getId());
        return mapToResponse(updatedAnswer);
    }

    @Transactional
    public void deleteAnswer(UUID id, User requester) {
        log.info("Deletando resposta ID: {}", id);

        Answer answer = findAnswerOrThrow(id);

        boolean isAuthor = answer.getAuthor().getId().equals(requester.getId());
        boolean isAdmin = requester.isAdmin();

        if (!isAuthor && !isAdmin) {
            throw new ForbiddenException(ErrorCode.FORBIDDEN,
                    "Você não tem permissão para deletar esta resposta.");
        }

        // O trigger no banco atualiza automaticamente o answer_count
        answerRepository.delete(answer);
        log.info("Resposta deletada: {}", id);
    }

    @Transactional
    public AnswerResponse acceptAnswer(UUID id, User requester) {
        log.info("Aceitando resposta ID: {}", id);

        Answer answer = findAnswerOrThrow(id);
        Question question = answer.getQuestion();

        // Apenas o autor da pergunta pode aceitar
        if (!question.getAuthor().getId().equals(requester.getId())) {
            throw new ForbiddenException(ErrorCode.FORBIDDEN,
                    "Apenas o autor da pergunta pode aceitar uma resposta.");
        }

        answer.accept();
        Answer updatedAnswer = answerRepository.save(answer);

        log.info("Resposta aceita: {}", id);
        return mapToResponse(updatedAnswer);
    }

    // ==================== Métodos Privados ====================

    private Answer findAnswerOrThrow(UUID id) {
        return answerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.ANSWER_NOT_FOUND,
                        "Resposta não encontrada com ID: " + id));
    }

    private void validateAuthorPermission(Answer answer, User requester) {
        if (!answer.getAuthor().getId().equals(requester.getId())) {
            throw new ForbiddenException(ErrorCode.FORBIDDEN,
                    "Apenas o autor pode editar esta resposta.");
        }
    }

    private AnswerResponse mapToResponse(Answer answer) {
        return AnswerResponse.builder()
                .id(answer.getId())
                .questionId(answer.getQuestion().getId())
                .authorId(answer.getAuthor().getId())
                .authorName(answer.getAuthor().getName())
                .content(answer.getContent())
                .accepted(answer.isAccepted())
                .createdAt(answer.getCreatedAt())
                .updatedAt(answer.getUpdatedAt())
                .build();
    }
}