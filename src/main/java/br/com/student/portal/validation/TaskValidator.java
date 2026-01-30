package br.com.student.portal.validation;

import br.com.student.portal.entity.CourseEntity;
import br.com.student.portal.entity.TaskEntity;
import br.com.student.portal.exception.BadRequestException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static br.com.student.portal.validation.FieldValidator.validateRequiredField;

@Component
public class TaskValidator {

    private static final String NAME_FIELD = "Nome";
    private static final String DEADLINE_FIELD = "Prazo";
    private static final String DESCRIPTION_FIELD = "Descrição";
    private static final String TITLE_FIELD = "Título";
    private static final String COURSE_FIELD = "Curso";

    public static void validateName(String name) {
        validateRequiredField(name, NAME_FIELD);
        if (name.trim().isEmpty()) {
            throw new BadRequestException(NAME_FIELD + " não pode estar vazio.");
        }
    }

    public static void validateDeadLine(LocalDateTime deadline) {
        validateRequiredField(deadline, DEADLINE_FIELD);
        if (deadline.isBefore(LocalDateTime.now())) {
            throw new BadRequestException(
                    DEADLINE_FIELD + " não pode ser uma data no passado.");
        }
    }

    public static void validateDescription(String description) {
        validateRequiredField(description, DESCRIPTION_FIELD);
        if (description.trim().isEmpty()) {
            throw new BadRequestException(DESCRIPTION_FIELD + " não pode estar vazio.");
        }
    }

    public static void validateTitle(String title) {
        validateRequiredField(title, TITLE_FIELD);
        if (title.trim().isEmpty()) {
            throw new BadRequestException(TITLE_FIELD + " não pode estar vazio.");
        }
    }

    public static void validateCourse(CourseEntity courseEntity) {
        if (courseEntity == null) {
            throw new BadRequestException(COURSE_FIELD + " é obrigatório.");
        }
        if (courseEntity.getId() == null) {
            throw new BadRequestException(COURSE_FIELD + " deve ter um ID válido.");
        }
    }

    public static void validateTaskFields(TaskEntity taskEntity) {
        validateRequiredField(taskEntity, "Task");
        validateName(taskEntity.getName());
        validateDeadLine(taskEntity.getDeadline());
        validateDescription(taskEntity.getDescription());
        validateTitle(taskEntity.getTitle());
        validateCourse(taskEntity.getCourseEntity());
    }
}