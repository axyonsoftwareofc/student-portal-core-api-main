package br.com.student.portal.exception.types;

import br.com.student.portal.exception.ErrorCode;
import br.com.student.portal.exception.base.BaseException;
import org.springframework.http.HttpStatus;

public class NotFoundException extends BaseException {

    public NotFoundException() {
        super(ErrorCode.RESOURCE_NOT_FOUND, HttpStatus.NOT_FOUND);
    }

    public NotFoundException(String message) {
        super(ErrorCode.RESOURCE_NOT_FOUND, HttpStatus.NOT_FOUND, message);
    }

    public NotFoundException(ErrorCode errorCode) {
        super(errorCode, HttpStatus.NOT_FOUND);
    }

    public NotFoundException(ErrorCode errorCode, String message) {
        super(errorCode, HttpStatus.NOT_FOUND, message);
    }
}