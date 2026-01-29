package br.com.student.portal.exception.types;

import br.com.student.portal.exception.ErrorCode;
import br.com.student.portal.exception.base.BaseException;
import org.springframework.http.HttpStatus;

/**
 * Exceção para erros de acesso proibido (403 Forbidden).
 * Usada quando o usuário está autenticado, mas não tem permissão para o recurso.
 */
public class ForbiddenException extends BaseException {

    public ForbiddenException() {
        super(ErrorCode.FORBIDDEN, HttpStatus.FORBIDDEN);
    }

    public ForbiddenException(String message) {
        super(ErrorCode.FORBIDDEN, HttpStatus.FORBIDDEN, message);
    }

    public ForbiddenException(ErrorCode errorCode) {
        super(errorCode, HttpStatus.FORBIDDEN);
    }

    public ForbiddenException(ErrorCode errorCode, String message) {
        super(errorCode, HttpStatus.FORBIDDEN, message);
    }

    /**
     * Cria exceção para permissão insuficiente com detalhes.
     */
    public static ForbiddenException insufficientPermissions(String resource, String requiredRole) {
        return (ForbiddenException) new ForbiddenException(
                ErrorCode.INSUFFICIENT_PERMISSIONS,
                String.format("Permissão insuficiente para acessar '%s'. Requer: %s", resource, requiredRole)
        ).addDetail("resource", resource)
                .addDetail("requiredRole", requiredRole);
    }

    /**
     * Cria exceção para operação não permitida.
     */
    public static ForbiddenException operationNotAllowed(String operation) {
        return (ForbiddenException) new ForbiddenException(
                ErrorCode.OPERATION_NOT_ALLOWED,
                String.format("Operação '%s' não é permitida", operation)
        ).addDetail("operation", operation);
    }
}