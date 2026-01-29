package br.com.student.portal.exception.base;

import br.com.student.portal.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

@Getter
public abstract class BaseException extends RuntimeException {

    private final ErrorCode errorCode;
    private final HttpStatus httpStatus;
    private final Map<String, Object> details;

    protected BaseException(ErrorCode errorCode, HttpStatus httpStatus) {
        super(errorCode.getDefaultMessage());
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.details = new HashMap<>();
    }

    protected BaseException(ErrorCode errorCode, HttpStatus httpStatus, String customMessage) {
        super(customMessage);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.details = new HashMap<>();
    }

    protected BaseException(ErrorCode errorCode, HttpStatus httpStatus, String customMessage, Throwable cause) {
        super(customMessage, cause);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.details = new HashMap<>();
    }

    public BaseException addDetail(String key, Object value) {
        this.details.put(key, value);
        return this;
    }
}