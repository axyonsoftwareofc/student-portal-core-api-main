package br.com.student.portal.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // Erros Gerais (1000-1099)
    INTERNAL_ERROR("ERR-1000", "Erro interno do servidor"),
    INVALID_REQUEST("ERR-1001", "Requisição inválida"),
    VALIDATION_ERROR("ERR-1002", "Erro de validação"),

    // Erros de Autenticação/Autorização (1100-1199)
    UNAUTHORIZED("ERR-1100", "Não autenticado"),
    FORBIDDEN("ERR-1101", "Acesso negado"),
    INVALID_CREDENTIALS("ERR-1102", "Credenciais inválidas"),
    TOKEN_EXPIRED("ERR-1103", "Token expirado"),
    TOKEN_INVALID("ERR-1104", "Token inválido"),

    // Erros de Recurso (1200-1299)
    RESOURCE_NOT_FOUND("ERR-1200", "Recurso não encontrado"),
    USER_NOT_FOUND("ERR-1201", "Usuário não encontrado"),
    QUESTION_NOT_FOUND("ERR-1202", "Pergunta não encontrada"),
    ANSWER_NOT_FOUND("ERR-1203", "Resposta não encontrada"),
    MATERIAL_NOT_FOUND("ERR-1204", "Material não encontrado"),
    COURSE_NOT_FOUND("ERR-1205", "Curso não encontrado"),
    TASK_NOT_FOUND("ERR-1206", "Tarefa não encontrada"),

    // Erros de Conflito (1300-1399)
    RESOURCE_ALREADY_EXISTS("ERR-1300", "Recurso já existe"),
    EMAIL_ALREADY_EXISTS("ERR-1301", "Email já cadastrado"),
    REGISTRATION_ALREADY_EXISTS("ERR-1302", "Matrícula já cadastrada"),

    // Erros de Validação de Campos (1400-1499)
    FIELD_REQUIRED("ERR-1400", "Campo obrigatório"),
    FIELD_INVALID_FORMAT("ERR-1401", "Formato inválido"),
    FIELD_TOO_SHORT("ERR-1402", "Campo muito curto"),
    FIELD_TOO_LONG("ERR-1403", "Campo muito longo"),
    INVALID_EMAIL("ERR-1404", "Email inválido"),
    INVALID_PASSWORD("ERR-1405", "Senha não atende os requisitos");

    private final String code;
    private final String defaultMessage;
}