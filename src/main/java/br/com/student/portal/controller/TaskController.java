package br.com.student.portal.controller;

import br.com.student.portal.dto.request.TaskRequest;
import br.com.student.portal.dto.response.TaskResponse;
import br.com.student.portal.entity.User;
import br.com.student.portal.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Tag(name = "Tarefas", description = "Gerenciamento de tarefas")
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    @Operation(summary = "Lista todas as tarefas")
    public ResponseEntity<List<TaskResponse>> getAllTasks() {
        return ResponseEntity.ok(taskService.getAllTasks());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca tarefa por ID")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable UUID id) {
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    @GetMapping("/course/{courseId}")
    @Operation(summary = "Lista tarefas de um curso")
    public ResponseEntity<List<TaskResponse>> getTasksByCourse(@PathVariable UUID courseId) {
        return ResponseEntity.ok(taskService.getTasksByCourse(courseId));
    }

    @GetMapping("/upcoming")
    @Operation(summary = "Lista tarefas com prazo próximo")
    public ResponseEntity<List<TaskResponse>> getUpcomingTasks(
            @RequestParam(defaultValue = "7") int days) {
        return ResponseEntity.ok(taskService.getUpcomingTasks(days));
    }

    @GetMapping("/overdue")
    @Operation(summary = "Lista tarefas atrasadas")
    public ResponseEntity<List<TaskResponse>> getOverdueTasks() {
        return ResponseEntity.ok(taskService.getOverdueTasks());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "Cria uma nova tarefa")
    public ResponseEntity<TaskResponse> createTask(
            @Valid @RequestBody TaskRequest request,
            Authentication authentication) {

        User createdBy = (User) authentication.getPrincipal();
        TaskResponse response = taskService.createTask(request, createdBy);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "Atualiza uma tarefa")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable UUID id,
            @Valid @RequestBody TaskRequest request) {

        TaskResponse response = taskService.updateTask(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "Remove uma tarefa")
    public ResponseEntity<Void> deleteTask(@PathVariable UUID id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}