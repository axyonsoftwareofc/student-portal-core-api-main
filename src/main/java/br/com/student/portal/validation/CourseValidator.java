package br.com.student.portal.validation;

import br.com.student.portal.dto.request.CourseRequest;
import br.com.student.portal.entity.CourseEntity;
import br.com.student.portal.exception.BadRequestException;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

import static br.com.student.portal.validation.FieldValidator.*;

@Component
public class CourseValidator {

    private static final String NAME_FIELD = "Nome";
    private static final String DESCRIPTION_FIELD = "Descrição";
    private static final String START_DATE_FIELD = "Data de início";
    private static final String END_DATE_FIELD = "Data de término";

    public static void validateName(String name) {
        validateRequiredField(name, NAME_FIELD);
        validateMinLength(name, NAME_FIELD, 3);
        validateMaxLength(name, NAME_FIELD, 255);
    }

    public static void validateDescription(String description) {
        if (description != null) {
            validateMaxLength(description, DESCRIPTION_FIELD, 2000);
        }
    }

    public static void validateDates(LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null) {
            if (endDate.isBefore(startDate)) {
                throw new BadRequestException(
                        END_DATE_FIELD + " não pode ser anterior a " + START_DATE_FIELD + ".");
            }
        }
    }

    public static void validateCourseRequest(CourseRequest request) {
        validateRequiredField(request, "CourseRequest");
        validateName(request.getName());
        validateDescription(request.getDescription());
        validateDates(request.getStartDate(), request.getEndDate());
    }

    public static void validateCourse(CourseEntity courseEntity) {
        validateRequiredField(courseEntity, "Course");
        validateName(courseEntity.getName());
        validateDescription(courseEntity.getDescription());
        validateDates(courseEntity.getStartDate(), courseEntity.getEndDate());
    }
}