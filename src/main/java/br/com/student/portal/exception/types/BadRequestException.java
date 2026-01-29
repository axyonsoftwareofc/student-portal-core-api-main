package br.com.student.portal.exception.types;

import br.com.student.portal.exception.ErrorCode;
import br.com.student.portal.exception.base.BaseException;
import org.springframework.http.HttpStatus;

public class BadRequestException extends BaseException {

    public BadRequestException() {
        super(ErrorCode.INVALID_REQUEST, HttpStatus.BAD_REQUEST);
    }

    public BadRequestException(String message) {
        super(ErrorCode.INVALID_REQUEST, HttpStatus.BAD_REQUEST, message);
    }

    public BadRequestException(ErrorCode errorCode) {
        super(errorCode, HttpStatus.BAD_REQUEST);
    }

    public BadRequestException(ErrorCode errorCode, String message) {
        super(errorCode, HttpStatus.BAD_REQUEST, message);
    }
}