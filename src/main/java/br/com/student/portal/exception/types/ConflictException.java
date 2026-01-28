package br.com.student.portal.exception.types;

import br.com.student.portal.exception.ErrorCode;
import br.com.student.portal.exception.base.BaseException;
import org.springframework.http.HttpStatus;

public class ConflictException extends BaseException {

    public ConflictException() {
        super(ErrorCode.RESOURCE_ALREADY_EXISTS, HttpStatus.CONFLICT);
    }

    public ConflictException(String message) {
        super(ErrorCode.RESOURCE_ALREADY_EXISTS, HttpStatus.CONFLICT, message);
    }

    public ConflictException(ErrorCode errorCode) {
        super(errorCode, HttpStatus.CONFLICT);
    }

    public ConflictException(ErrorCode errorCode, String message) {
        super(errorCode, HttpStatus.CONFLICT, message);
    }
}