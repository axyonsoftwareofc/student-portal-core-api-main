package br.com.student.portal.service.task;

import br.com.student.portal.dto.request.TaskRequest;
import br.com.student.portal.dto.response.TaskResponse;
import br.com.student.portal.entity.Course;
import br.com.student.portal.entity.Task;
import br.com.student.portal.entity.User;
import br.com.student.portal.entity.enums.TaskStatus;
import br.com.student.portal.exception.ErrorCode;
import br.com.student.portal.exception.types.NotFoundException;
import br.com.student.portal.repository.CourseRepository;
import br.com.student.portal.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final CourseRepository courseRepository;

    @Transactional(readOnly = true)
    public TaskResponse getTaskById(UUID id) {
        log.debug("Buscando tarefa por ID: {}", id);
        Task task = findTaskOrThrow(id);
        return mapToResponse(task);
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getAllTasks() {
        log.debug("Buscando todas as tarefas");
        return taskRepository.findAllByOrderByDeadlineAsc().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getTasksByCourse(UUID courseId) {
        log.debug("Buscando tarefas do curso: {}", courseId);
        return taskRepository.findByCourseId(courseId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getUpcomingTasks(int days) {
        log.debug("Buscando tarefas para os próximos {} dias", days);
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(days);
        return taskRepository.findUpcomingTasks(start, end).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getOverdueTasks() {
        log.debug("Buscando tarefas atrasadas");
        return taskRepository.findOverdueTasks(LocalDateTime.now()).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public TaskResponse createTask(TaskRequest request, User createdBy) {
        log.info("Criando nova tarefa: {}", request.getTitle());

        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new NotFoundException(ErrorCode.COURSE_NOT_FOUND,
                        "Curso não encontrado com ID: " + request.getCourseId()));

        Task task = Task.builder()
                .title(request.getTitle())
                .name(request.getName())
                .description(request.getDescription())
                .deadline(request.getDeadline())
                .course(course)
                .createdBy(createdBy)
                .status(TaskStatus.PENDING)
                .build();

        Task savedTask = taskRepository.save(task);
        log.info("Tarefa criada com ID: {}", savedTask.getId());

        return mapToResponse(savedTask);
    }

    @Transactional
    public TaskResponse updateTask(UUID id, TaskRequest request) {
        log.info("Atualizando tarefa ID: {}", id);

        Task task = findTaskOrThrow(id);

        task.setTitle(request.getTitle());
        task.setName(request.getName());
        task.setDescription(request.getDescription());
        task.setDeadline(request.getDeadline());

        if (request.getCourseId() != null &&
                !request.getCourseId().equals(task.getCourse().getId())) {
            Course newCourse = courseRepository.findById(request.getCourseId())
                    .orElseThrow(() -> new NotFoundException(ErrorCode.COURSE_NOT_FOUND,
                            "Curso não encontrado com ID: " + request.getCourseId()));
            task.setCourse(newCourse);
        }

        Task updatedTask = taskRepository.save(task);
        log.info("Tarefa atualizada: {}", updatedTask.getId());

        return mapToResponse(updatedTask);
    }

    @Transactional
    public void deleteTask(UUID id) {
        log.info("Deletando tarefa ID: {}", id);
        Task task = findTaskOrThrow(id);
        taskRepository.delete(task);
        log.info("Tarefa deletada: {}", id);
    }

    // ==================== Métodos Privados ====================

    private Task findTaskOrThrow(UUID id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.TASK_NOT_FOUND,
                        "Tarefa não encontrada com ID: " + id));
    }

    private TaskResponse mapToResponse(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .name(task.getName())
                .description(task.getDescription())
                .deadline(task.getDeadline())
                .status(task.getStatus())
                .statusDisplayName(task.getStatus().getDisplayName())
                .courseId(task.getCourse().getId())
                .courseName(task.getCourse().getName())
                .createdById(task.getCreatedBy() != null ? task.getCreatedBy().getId() : null)
                .createdByName(task.getCreatedBy() != null ? task.getCreatedBy().getName() : null)
                .isOverdue(task.isOverdue())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }
}