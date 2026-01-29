package br.com.student.portal.exception.types;

import br.com.student.portal.exception.ErrorCode;
import br.com.student.portal.exception.base.BaseException;
import org.springframework.http.HttpStatus;

public class UnauthorizedException extends BaseException {

    public UnauthorizedException() {
        super(ErrorCode.UNAUTHORIZED, HttpStatus.UNAUTHORIZED);
    }

    public UnauthorizedException(String message) {
        super(ErrorCode.UNAUTHORIZED, HttpStatus.UNAUTHORIZED, message);
    }

    public UnauthorizedException(ErrorCode errorCode) {
        super(errorCode, HttpStatus.UNAUTHORIZED);
    }

    public UnauthorizedException(ErrorCode errorCode, String message) {
        super(errorCode, HttpStatus.UNAUTHORIZED, message);
    }
}