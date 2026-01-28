package br.com.student.portal.validation;

import br.com.student.portal.exception.ErrorCode;
import br.com.student.portal.exception.types.BadRequestException;
import org.springframework.stereotype.Component;

import static io.micrometer.common.util.StringUtils.isEmpty;

@Component
public class FieldValidator {

    public static void validateRequiredField(Object fieldValue, String fieldName) {
        if (fieldValue == null || (fieldValue instanceof String && isEmpty((String) fieldValue))) {
            throw new BadRequestException(ErrorCode.FIELD_REQUIRED, fieldName + " é um campo obrigatório.");
        }
    }

    public static void validateMinLength(String value, String fieldName, int minLength) {
        if (value != null && value.length() < minLength) {
            throw new BadRequestException(ErrorCode.FIELD_TOO_SHORT,
                    fieldName + " deve ter no mínimo " + minLength + " caracteres.");
        }
    }

    public static void validateMaxLength(String value, String fieldName, int maxLength) {
        if (value != null && value.length() > maxLength) {
            throw new BadRequestException(ErrorCode.FIELD_TOO_LONG,
                    fieldName + " deve ter no máximo " + maxLength + " caracteres.");
        }
    }
}