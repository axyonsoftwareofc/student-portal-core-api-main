package br.com.student.portal.exception.types;

import br.com.student.portal.exception.ErrorCode;
import br.com.student.portal.exception.base.BaseException;
import org.springframework.http.HttpStatus;

public class InternalServerException extends BaseException {

    public InternalServerException() {
        super(ErrorCode.INTERNAL_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public InternalServerException(String message) {
        super(ErrorCode.INTERNAL_ERROR, HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

    public InternalServerException(String message, Throwable cause) {
        super(ErrorCode.INTERNAL_ERROR, HttpStatus.INTERNAL_SERVER_ERROR, message, cause);
    }
}