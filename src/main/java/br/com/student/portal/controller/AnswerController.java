package br.com.student.portal.controller;

import br.com.student.portal.dto.request.AnswerRequest;
import br.com.student.portal.dto.response.AnswerResponse;
import br.com.student.portal.entity.User;
import br.com.student.portal.service.question.AnswerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/answers")
@RequiredArgsConstructor
@Tag(name = "Respostas", description = "Gerenciamento de respostas às perguntas")
public class AnswerController {

    private final AnswerService answerService;

    @GetMapping("/{id}")
    @Operation(summary = "Busca resposta por ID")
    public ResponseEntity<AnswerResponse> getAnswerById(@PathVariable UUID id) {
        return ResponseEntity.ok(answerService.getAnswerById(id));
    }

    @GetMapping("/question/{questionId}")
    @Operation(summary = "Lista respostas de uma pergunta")
    public ResponseEntity<List<AnswerResponse>> getAnswersByQuestion(@PathVariable UUID questionId) {
        return ResponseEntity.ok(answerService.getAnswersByQuestionId(questionId));
    }

    @GetMapping("/author/{authorId}")
    @Operation(summary = "Lista respostas de um autor")
    public ResponseEntity<List<AnswerResponse>> getAnswersByAuthor(@PathVariable UUID authorId) {
        return ResponseEntity.ok(answerService.getAnswersByAuthor(authorId));
    }

    @GetMapping("/question/{questionId}/count")
    @Operation(summary = "Conta respostas de uma pergunta")
    public ResponseEntity<Long> countAnswersByQuestion(@PathVariable UUID questionId) {
        return ResponseEntity.ok(answerService.countAnswersByQuestionId(questionId));
    }

    @PostMapping("/question/{questionId}")
    @Operation(summary = "Cria uma resposta para uma pergunta")
    public ResponseEntity<AnswerResponse> createAnswer(
            @PathVariable UUID questionId,
            @Valid @RequestBody AnswerRequest request,
            Authentication authentication) {

        User author = (User) authentication.getPrincipal();
        AnswerResponse response = answerService.createAnswer(request, questionId, author);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza uma resposta")
    public ResponseEntity<AnswerResponse> updateAnswer(
            @PathVariable UUID id,
            @Valid @RequestBody AnswerRequest request,
            Authentication authentication) {

        User requester = (User) authentication.getPrincipal();
        AnswerResponse response = answerService.updateAnswer(id, request, requester);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove uma resposta")
    public ResponseEntity<Void> deleteAnswer(
            @PathVariable UUID id,
            Authentication authentication) {

        User requester = (User) authentication.getPrincipal();
        answerService.deleteAnswer(id, requester);
        return ResponseEntity.noContent().build();
    }
}