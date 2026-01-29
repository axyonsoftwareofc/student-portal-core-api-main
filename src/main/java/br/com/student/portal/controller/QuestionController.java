package br.com.student.portal.controller;

import br.com.student.portal.dto.request.QuestionRequest;
import br.com.student.portal.dto.response.QuestionResponse;
import br.com.student.portal.entity.User;
import br.com.student.portal.service.question.QuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
@Tag(name = "Perguntas", description = "Gerenciamento de perguntas do fórum")
public class QuestionController {

    private final QuestionService questionService;

    @GetMapping
    @Operation(summary = "Lista todas as perguntas")
    public ResponseEntity<List<QuestionResponse>> getAllQuestions() {
        return ResponseEntity.ok(questionService.getAllQuestions());
    }

    @GetMapping("/paginated")
    @Operation(summary = "Lista perguntas com paginação")
    public ResponseEntity<Page<QuestionResponse>> getAllQuestionsPaginated(
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(questionService.getAllQuestions(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca pergunta por ID")
    public ResponseEntity<QuestionResponse> getQuestionById(@PathVariable UUID id) {
        return ResponseEntity.ok(questionService.getQuestionById(id));
    }

    @GetMapping("/search")
    @Operation(summary = "Busca perguntas por termo")
    public ResponseEntity<List<QuestionResponse>> searchQuestions(@RequestParam String term) {
        return ResponseEntity.ok(questionService.searchQuestions(term));
    }

    @PostMapping
    @Operation(summary = "Cria uma nova pergunta")
    public ResponseEntity<QuestionResponse> createQuestion(
            @Valid @RequestBody QuestionRequest request,
            Authentication authentication) {

        User author = (User) authentication.getPrincipal();
        QuestionResponse response = questionService.createQuestion(request, author);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza uma pergunta")
    public ResponseEntity<QuestionResponse> updateQuestion(
            @PathVariable UUID id,
            @Valid @RequestBody QuestionRequest request,
            Authentication authentication) {

        User requester = (User) authentication.getPrincipal();
        QuestionResponse response = questionService.updateQuestion(id, request, requester);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove uma pergunta")
    public ResponseEntity<Void> deleteQuestion(
            @PathVariable UUID id,
            Authentication authentication) {

        User requester = (User) authentication.getPrincipal();
        questionService.deleteQuestion(id, requester);
        return ResponseEntity.noContent().build();
    }
}