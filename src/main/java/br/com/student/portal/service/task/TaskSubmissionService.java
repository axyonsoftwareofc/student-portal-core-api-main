package br.com.student.portal.service.task;

import br.com.student.portal.dto.request.GradeSubmissionRequest;
import br.com.student.portal.dto.request.TaskSubmissionRequest;
import br.com.student.portal.dto.response.TaskSubmissionResponse;
import br.com.student.portal.entity.Task;
import br.com.student.portal.entity.TaskSubmission;
import br.com.student.portal.entity.User;
import br.com.student.portal.entity.enums.SubmissionStatus;
import br.com.student.portal.exception.ErrorCode;
import br.com.student.portal.exception.types.BadRequestException;
import br.com.student.portal.exception.types.ConflictException;
import br.com.student.portal.exception.types.ForbiddenException;
import br.com.student.portal.exception.types.NotFoundException;
import br.com.student.portal.repository.TaskRepository;
import br.com.student.portal.repository.TaskSubmissionRepository;
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
public class TaskSubmissionService {

    private final TaskSubmissionRepository submissionRepository;
    private final TaskRepository taskRepository;

    @Transactional(readOnly = true)
    public TaskSubmissionResponse getSubmissionById(UUID id) {
        log.debug("Buscando submissão por ID: {}", id);
        TaskSubmission submission = findSubmissionOrThrow(id);
        return mapToResponse(submission);
    }

    @Transactional(readOnly = true)
    public List<TaskSubmissionResponse> getSubmissionsByTask(UUID taskId) {
        log.debug("Buscando submissões da tarefa: {}", taskId);
        return submissionRepository.findByTaskId(taskId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TaskSubmissionResponse> getSubmissionsByStudent(UUID studentId) {
        log.debug("Buscando submissões do estudante: {}", studentId);
        return submissionRepository.findByStudentId(studentId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public TaskSubmissionResponse getSubmissionByTaskAndStudent(UUID taskId, UUID studentId) {
        log.debug("Buscando submissão - Tarefa: {}, Estudante: {}", taskId, studentId);
        return submissionRepository.findByTaskIdAndStudentId(taskId, studentId)
                .map(this::mapToResponse)
                .orElse(null);
    }

    @Transactional
    public TaskSubmissionResponse submitTask(TaskSubmissionRequest request, User student) {
        log.info("Submetendo tarefa: {} por estudante: {}", request.getTaskId(), student.getId());

        Task task = taskRepository.findById(request.getTaskId())
                .orElseThrow(() -> new NotFoundException(ErrorCode.TASK_NOT_FOUND,
                        "Tarefa não encontrada"));

        // Verificar se já submeteu
        if (submissionRepository.hasStudentSubmitted(task.getId(), student.getId())) {
            throw new ConflictException(ErrorCode.TASK_ALREADY_SUBMITTED,
                    "Você já enviou esta tarefa.");
        }

        TaskSubmission submission = TaskSubmission.builder()
                .task(task)
                .student(student)
                .content(request.getContent())
                .fileUrl(request.getFileUrl())
                .submittedAt(LocalDateTime.now())
                .status(SubmissionStatus.SUBMITTED)
                .build();

        TaskSubmission savedSubmission = submissionRepository.save(submission);
        log.info("Submissão criada com ID: {}", savedSubmission.getId());

        return mapToResponse(savedSubmission);
    }

    @Transactional
    public TaskSubmissionResponse gradeSubmission(UUID id, GradeSubmissionRequest request, User grader) {
        log.info("Avaliando submissão ID: {}", id);

        TaskSubmission submission = findSubmissionOrThrow(id);

        // Verificar permissão (professor ou admin)
        if (!grader.isAdmin() && !grader.isTeacher()) {
            throw new ForbiddenException(ErrorCode.FORBIDDEN,
                    "Apenas professores podem avaliar submissões.");
        }

        submission.assignGrade(request.getGrade(), request.getFeedback(), grader);
        TaskSubmission updatedSubmission = submissionRepository.save(submission);

        log.info("Submissão avaliada: {} - Nota: {}", id, request.getGrade());
        return mapToResponse(updatedSubmission);
    }

    @Transactional
    public TaskSubmissionResponse returnSubmission(UUID id, String feedback, User grader) {
        log.info("Devolvendo submissão ID: {}", id);

        TaskSubmission submission = findSubmissionOrThrow(id);

        if (!grader.isAdmin() && !grader.isTeacher()) {
            throw new ForbiddenException(ErrorCode.FORBIDDEN,
                    "Apenas professores podem devolver submissões.");
        }

        submission.returnForRevision(feedback, grader);
        TaskSubmission updatedSubmission = submissionRepository.save(submission);

        log.info("Submissão devolvida: {}", id);
        return mapToResponse(updatedSubmission);
    }

    @Transactional
    public TaskSubmissionResponse resubmitTask(UUID id, TaskSubmissionRequest request, User student) {
        log.info("Resubmetendo tarefa ID: {}", id);

        TaskSubmission submission = findSubmissionOrThrow(id);

        // Verificar se é o estudante correto
        if (!submission.getStudent().getId().equals(student.getId())) {
            throw new ForbiddenException(ErrorCode.FORBIDDEN,
                    "Você não pode editar esta submissão.");
        }

        // Verificar se permite resubmissão
        if (!submission.getStatus().allowsResubmission()) {
            throw new BadRequestException(ErrorCode.OPERATION_NOT_ALLOWED,
                    "Esta submissão não pode ser reenviada.");
        }

        submission.setContent(request.getContent());
        submission.setFileUrl(request.getFileUrl());
        submission.setSubmittedAt(LocalDateTime.now());
        submission.setStatus(SubmissionStatus.SUBMITTED);

        TaskSubmission updatedSubmission = submissionRepository.save(submission);

        log.info("Submissão reenviada: {}", id);
        return mapToResponse(updatedSubmission);
    }

    // ==================== Métodos Privados ====================

    private TaskSubmission findSubmissionOrThrow(UUID id) {
        return submissionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.RESOURCE_NOT_FOUND,
                        "Submissão não encontrada com ID: " + id));
    }

    private TaskSubmissionResponse mapToResponse(TaskSubmission submission) {
        return TaskSubmissionResponse.builder()
                .id(submission.getId())
                .taskId(submission.getTask().getId())
                .taskTitle(submission.getTask().getTitle())
                .studentId(submission.getStudent().getId())
                .studentName(submission.getStudent().getName())
                .content(submission.getContent())
                .fileUrl(submission.getFileUrl())
                .submittedAt(submission.getSubmittedAt())
                .grade(submission.getGrade())
                .feedback(submission.getFeedback())
                .gradedById(submission.getGradedBy() != null ? submission.getGradedBy().getId() : null)
                .gradedByName(submission.getGradedBy() != null ? submission.getGradedBy().getName() : null)
                .gradedAt(submission.getGradedAt())
                .status(submission.getStatus())
                .statusDisplayName(submission.getStatus().getDisplayName())
                .late(submission.isLate())
                .build();
    }
}