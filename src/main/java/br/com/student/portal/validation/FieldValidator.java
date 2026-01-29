package br.com.student.portal.validation;

import br.com.student.portal.exception.ErrorCode;
import br.com.student.portal.exception.types.BadRequestException;
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
            throw new BadRequestException(ErrorCode.FIELD_REQUIRED,
                    fieldName + " é um campo obrigatório.");
        }

        if (fieldValue instanceof String && ((String) fieldValue).isBlank()) {
            throw new BadRequestException(ErrorCode.FIELD_REQUIRED,
                    fieldName + " é um campo obrigatório.");
        }

        if (fieldValue instanceof Collection && ((Collection<?>) fieldValue).isEmpty()) {
            throw new BadRequestException(ErrorCode.FIELD_REQUIRED,
                    fieldName + " não pode estar vazio.");
        }
    }

    /**
     * Valida tamanho mínimo de uma string.
     */
    public static void validateMinLength(String value, String fieldName, int minLength) {
        if (value != null && value.length() < minLength) {
            throw new BadRequestException(ErrorCode.FIELD_TOO_SHORT,
                    fieldName + " deve ter no mínimo " + minLength + " caracteres.");
        }
    }

    /**
     * Valida tamanho máximo de uma string.
     */
    public static void validateMaxLength(String value, String fieldName, int maxLength) {
        if (value != null && value.length() > maxLength) {
            throw new BadRequestException(ErrorCode.FIELD_TOO_LONG,
                    fieldName + " deve ter no máximo " + maxLength + " caracteres.");
        }
    }

    /**
     * Valida se uma string corresponde a um padrão regex.
     */
    public static void validatePattern(String value, String fieldName, String regex, String message) {
        if (value != null && !value.matches(regex)) {
            throw new BadRequestException(ErrorCode.FIELD_INVALID_FORMAT, message);
        }
    }

    /**
     * Valida se um UUID é válido.
     */
    public static void validateUUID(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new BadRequestException(ErrorCode.FIELD_REQUIRED,
                    fieldName + " é obrigatório.");
        }

        try {
            UUID.fromString(value);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(ErrorCode.FIELD_INVALID_FORMAT,
                    fieldName + " deve ser um UUID válido.");
        }
    }

    /**
     * Valida se um número é positivo.
     */
    public static void validatePositive(Number value, String fieldName) {
        if (value != null && value.doubleValue() <= 0) {
            throw new BadRequestException(ErrorCode.FIELD_INVALID_FORMAT,
                    fieldName + " deve ser um valor positivo.");
        }
    }

    /**
     * Verifica se uma string está vazia ou nula.
     */
    public static boolean isEmpty(String value) {
        return value == null || value.isBlank();
    }

    /**
     * Verifica se uma string não está vazia.
     */
    public static boolean isNotEmpty(String value) {
        return value != null && !value.isBlank();
    }
}