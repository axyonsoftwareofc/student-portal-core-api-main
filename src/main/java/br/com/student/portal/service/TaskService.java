package br.com.student.portal.service;

import br.com.student.portal.dto.request.TaskRequest;
import br.com.student.portal.dto.response.TaskResponse;
import br.com.student.portal.entity.CourseEntity;
import br.com.student.portal.entity.TaskEntity;
import br.com.student.portal.entity.UserEntity;
import br.com.student.portal.entity.enums.TaskStatus;
import br.com.student.portal.exception.ObjectNotFoundException;
import br.com.student.portal.repository.CourseRepository;
import br.com.student.portal.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        TaskEntity taskEntity = findTaskOrThrow(id);
        return mapToResponse(taskEntity);
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

    @Transactional
    public TaskResponse createTask(TaskRequest taskRequest, UserEntity createdBy) {
        log.info("Criando nova tarefa: {}", taskRequest.getTitle());

        var course = courseRepository.findById(taskRequest.getCourseId())
                .orElseThrow(() -> new ObjectNotFoundException(
                        "Curso não encontrado com ID: " + taskRequest.getCourseId()));

        //TODO:MOVER PARA UM BUILDER
        var task = TaskEntity.builder()
                .title(taskRequest.getTitle())
                .name(taskRequest.getName())
                .description(taskRequest.getDescription())
                .deadline(taskRequest.getDeadline())
                .courseEntity(course)
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
                !taskRequest.getCourseId().equals(task.getCourseEntity().getId())) {
            CourseEntity newCourseEntity = courseRepository.findById(taskRequest.getCourseId())
                    .orElseThrow(() -> new ObjectNotFoundException(
                            "Curso não encontrado com ID: " + taskRequest.getCourseId()));
            task.setCourseEntity(newCourseEntity);
        }

        validateTaskFields(task);

        var updatedTask = taskRepository.save(task);
        log.info("Tarefa atualizada: {}", updatedTask.getId());

        return mapToResponse(updatedTask);
    }

    @Transactional
    public void deleteTask(UUID id) {
        log.info("Deletando tarefa ID: {}", id);
        TaskEntity taskEntity = findTaskOrThrow(id);
        taskRepository.delete(taskEntity);
        log.info("Tarefa deletada: {}", id);
    }

    private TaskEntity findTaskOrThrow(UUID id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException(
                        "Tarefa não encontrada com ID: " + id));
    }

    //TODO:MOVER ESSA FUNÇÃO PARA UM MAPPER
    private TaskResponse mapToResponse(TaskEntity taskEntity) {
        return TaskResponse.builder()
                .id(taskEntity.getId())
                .title(taskEntity.getTitle())
                .name(taskEntity.getName())
                .description(taskEntity.getDescription())
                .deadline(taskEntity.getDeadline())
                .status(taskEntity.getStatus())
                .statusDisplayName(taskEntity.getStatus().getDisplayName())
                .courseId(taskEntity.getCourseEntity().getId())
                .courseName(taskEntity.getCourseEntity().getName())
                .createdById(taskEntity.getCreatedBy() != null ? taskEntity.getCreatedBy().getId() : null)
                .createdByName(taskEntity.getCreatedBy() != null ? taskEntity.getCreatedBy().getName() : null)
                .overdue(taskEntity.isOverdue())
                .createdAt(taskEntity.getCreatedAt())
                .updatedAt(taskEntity.getUpdatedAt())
                .build();
    }
}