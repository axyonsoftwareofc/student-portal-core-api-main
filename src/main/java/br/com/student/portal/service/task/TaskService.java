package br.com.student.portal.service.task;

import br.com.student.portal.entity.Task;
import br.com.student.portal.exception.ErrorCode;
import br.com.student.portal.exception.types.NotFoundException;
import br.com.student.portal.repository.TaskRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static br.com.student.portal.validation.TaskValidator.validateTaskFields;

@Slf4j
@Service
@AllArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    public Task findTaskById(UUID id) {
        log.debug("Buscando tarefa por ID: {}", id);
        return taskRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.TASK_NOT_FOUND,
                        "Tarefa não encontrada com ID: " + id));
    }

    public List<Task> getAllTasks() {
        log.debug("Buscando todas as tarefas");
        return taskRepository.findAll();
    }

    public Task createTask(Task task) {
        log.info("Criando nova tarefa: {}", task.getTitle());
        validateTaskFields(task);
        Task savedTask = taskRepository.save(task);
        log.info("Tarefa criada com ID: {}", savedTask.getId());
        return savedTask;
    }

    public Task updateTask(UUID id, Task taskDetails) {
        log.info("Atualizando tarefa ID: {}", id);

        Task existingTask = findTaskById(id);
        validateTaskFields(taskDetails);

        existingTask.setName(taskDetails.getName());
        existingTask.setTitle(taskDetails.getTitle());
        existingTask.setDescription(taskDetails.getDescription());
        existingTask.setDeadline(taskDetails.getDeadline());
        existingTask.setCourse(taskDetails.getCourse());
        existingTask.setReceived(taskDetails.getReceived());

        Task updatedTask = taskRepository.save(existingTask);
        log.info("Tarefa atualizada: {}", updatedTask.getId());

        return updatedTask;
    }

    public void deleteTask(UUID id) {
        log.info("Deletando tarefa ID: {}", id);
        Task task = findTaskById(id);
        taskRepository.delete(task);
        log.info("Tarefa deletada: {}", id);
    }
}