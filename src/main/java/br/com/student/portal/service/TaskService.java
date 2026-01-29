package br.com.student.portal.service;

import br.com.student.portal.dto.request.TaskRequest;
import br.com.student.portal.dto.response.TaskResponse;
import br.com.student.portal.entity.Course;
import br.com.student.portal.entity.Task;
import br.com.student.portal.entity.User;
import br.com.student.portal.entity.enums.TaskStatus;
import br.com.student.portal.exception.ObjectNotFoundException;
import br.com.student.portal.repository.CourseRepository;
import br.com.student.portal.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static br.com.student.portal.validation.TaskValidator.validateTaskFields;

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
    public List<TaskResponse> getOverdueTasks() {
        log.debug("Buscando tarefas atrasadas");
        return taskRepository.findOverdueTasks(LocalDateTime.now()).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public TaskResponse createTask(TaskRequest taskRequest, User createdBy) {
        log.info("Criando nova tarefa: {}", taskRequest.getTitle());

        var course = courseRepository.findById(taskRequest.getCourseId())
                .orElseThrow(() -> new ObjectNotFoundException(
                        "Curso não encontrado com ID: " + taskRequest.getCourseId()));

        //TODO:MOVER PARA UM BUILDER
        var task = Task.builder()
                .title(taskRequest.getTitle())
                .name(taskRequest.getName())
                .description(taskRequest.getDescription())
                .deadline(taskRequest.getDeadline())
                .course(course)
                .createdBy(createdBy)
                .status(TaskStatus.PENDING)
                .build();

        validateTaskFields(task);

        var savedTask = taskRepository.save(task);
        log.info("Tarefa criada com ID: {}", savedTask.getId());

        return mapToResponse(savedTask);
    }

    @Transactional
    public TaskResponse updateTask(UUID id, TaskRequest taskRequest) {
        log.info("Atualizando tarefa ID: {}", id);

        var task = findTaskOrThrow(id);

        task.setTitle(taskRequest.getTitle());
        task.setName(taskRequest.getName());
        task.setDescription(taskRequest.getDescription());
        task.setDeadline(taskRequest.getDeadline());

        if (taskRequest.getCourseId() != null &&
                !taskRequest.getCourseId().equals(task.getCourse().getId())) {
            Course newCourse = courseRepository.findById(taskRequest.getCourseId())
                    .orElseThrow(() -> new ObjectNotFoundException(
                            "Curso não encontrado com ID: " + taskRequest.getCourseId()));
            task.setCourse(newCourse);
        }

        validateTaskFields(task);

        var updatedTask = taskRepository.save(task);
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

    private Task findTaskOrThrow(UUID id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException(
                        "Tarefa não encontrada com ID: " + id));
    }

    //TODO:MOVER ESSA FUNÇÃO PARA UM MAPPER
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
                .overdue(task.isOverdue())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }
}