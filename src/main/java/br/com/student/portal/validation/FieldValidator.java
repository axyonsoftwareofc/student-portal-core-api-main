package br.com.student.portal.validation;

import br.com.student.portal.exception.BadRequestException;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.UUID;

@Component
public class FieldValidator {

    /**
     * Valida se um campo obrigatório está preenchido.
     */
    public static void validateRequiredField(Object fieldValue, String fieldName) {
        if (fieldValue == null) {
            throw new BadRequestException(
                    fieldName + " é um campo obrigatório.");
        }

        if (fieldValue instanceof String && ((String) fieldValue).isBlank()) {
            throw new BadRequestException(
                    fieldName + " é um campo obrigatório.");
        }

        if (fieldValue instanceof Collection && ((Collection<?>) fieldValue).isEmpty()) {
            throw new BadRequestException(
                    fieldName + " não pode estar vazio.");
        }
    }


    public static void validateMinLength(String value, String fieldName, int minLength) {
        if (value != null && value.length() < minLength) {
            throw new BadRequestException(
                    fieldName + " deve ter no mínimo " + minLength + " caracteres.");
        }
    }

    public static void validateMaxLength(String value, String fieldName, int maxLength) {
        if (value != null && value.length() > maxLength) {
            throw new BadRequestException(
                    fieldName + " deve ter no máximo " + maxLength + " caracteres.");
        }
    }

    public static void validatePattern(String value, String fieldName, String regex, String message) {
        if (value != null && !value.matches(regex)) {
            throw new BadRequestException(message);
        }
    }

    public static void validateUUID(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new BadRequestException(
                    fieldName + " é obrigatório.");
        }

        try {
            UUID.fromString(value);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(
                    fieldName + " deve ser um UUID válido.");
        }
    }

    public static void validatePositive(Number value, String fieldName) {
        if (value != null && value.doubleValue() <= 0) {
            throw new BadRequestException(
                    fieldName + " deve ser um valor positivo.");
        }
    }

    public static boolean isEmpty(String value) {
        return value == null || value.isBlank();
    }

    public static boolean isNotEmpty(String value) {
        return value != null && !value.isBlank();
    }
}