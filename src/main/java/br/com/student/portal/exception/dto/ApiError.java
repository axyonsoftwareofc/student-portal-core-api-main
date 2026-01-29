package br.com.student.portal.exception.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * DTO padrão para respostas de erro da API.
 * Segue o padrão RFC 7807 (Problem Details for HTTP APIs).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    private Integer status;
    private String error;
    private String code;
    private String message;
    private String path;
    private Map<String, Object> details;
    private List<FieldError> fieldErrors;

    /**
     * Representa um erro de validação de campo específico.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FieldError {
        private String field;
        private String message;
        private Object rejectedValue;

        /**
         * Factory method para criar FieldError.
         */
        public static FieldError of(String field, String message) {
            return FieldError.builder()
                    .field(field)
                    .message(message)
                    .build();
        }

        public static FieldError of(String field, String message, Object rejectedValue) {
            return FieldError.builder()
                    .field(field)
                    .message(message)
                    .rejectedValue(rejectedValue)
                    .build();
        }
    }

    /**
     * Factory methods para criar ApiError rapidamente.
     */
    public static ApiError of(Integer status, String error, String code, String message, String path) {
        return ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(status)
                .error(error)
                .code(code)
                .message(message)
                .path(path)
                .build();
    }

    public static ApiError badRequest(String code, String message, String path) {
        return of(400, "Bad Request", code, message, path);
    }

    public static ApiError unauthorized(String code, String message, String path) {
        return of(401, "Unauthorized", code, message, path);
    }

    public static ApiError forbidden(String code, String message, String path) {
        return of(403, "Forbidden", code, message, path);
    }

    public static ApiError notFound(String code, String message, String path) {
        return of(404, "Not Found", code, message, path);
    }

    public static ApiError conflict(String code, String message, String path) {
        return of(409, "Conflict", code, message, path);
    }

    public static ApiError internalError(String code, String message, String path) {
        return of(500, "Internal Server Error", code, message, path);
    }
}