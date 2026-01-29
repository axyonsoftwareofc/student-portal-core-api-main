package br.com.student.portal.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // ==================== Erros Gerais (1000-1099) ====================
    INTERNAL_ERROR("ERR-1000", "Erro interno do servidor"),
    INVALID_REQUEST("ERR-1001", "Requisição inválida"),
    VALIDATION_ERROR("ERR-1002", "Erro de validação"),
    SERVICE_UNAVAILABLE("ERR-1003", "Serviço indisponível"),
    TIMEOUT_ERROR("ERR-1004", "Tempo de requisição esgotado"),

    // ==================== Erros de Autenticação/Autorização (1100-1199) ====================
    UNAUTHORIZED("ERR-1100", "Não autenticado"),
    FORBIDDEN("ERR-1101", "Acesso negado"),
    INVALID_CREDENTIALS("ERR-1102", "Credenciais inválidas"),
    TOKEN_EXPIRED("ERR-1103", "Token expirado"),
    TOKEN_INVALID("ERR-1104", "Token inválido"),
    ACCOUNT_DISABLED("ERR-1105", "Conta desativada"),
    ACCOUNT_LOCKED("ERR-1106", "Conta bloqueada"),
    PASSWORD_EXPIRED("ERR-1107", "Senha expirada"),
    SESSION_EXPIRED("ERR-1108", "Sessão expirada"),

    // ==================== Erros de Recurso (1200-1299) ====================
    RESOURCE_NOT_FOUND("ERR-1200", "Recurso não encontrado"),
    USER_NOT_FOUND("ERR-1201", "Usuário não encontrado"),
    QUESTION_NOT_FOUND("ERR-1202", "Pergunta não encontrada"),
    ANSWER_NOT_FOUND("ERR-1203", "Resposta não encontrada"),
    MATERIAL_NOT_FOUND("ERR-1204", "Material não encontrado"),
    COURSE_NOT_FOUND("ERR-1205", "Curso não encontrado"),
    TASK_NOT_FOUND("ERR-1206", "Tarefa não encontrada"),
    PAYMENT_NOT_FOUND("ERR-1207", "Pagamento não encontrado"),
    ENROLLMENT_NOT_FOUND("ERR-1208", "Matrícula não encontrada"),

    // ==================== Erros de Conflito (1300-1399) ====================
    RESOURCE_ALREADY_EXISTS("ERR-1300", "Recurso já existe"),
    EMAIL_ALREADY_EXISTS("ERR-1301", "Email já cadastrado"),
    REGISTRATION_ALREADY_EXISTS("ERR-1302", "Matrícula já cadastrada"),
    DUPLICATE_ENTRY("ERR-1303", "Entrada duplicada"),
    CONCURRENT_MODIFICATION("ERR-1304", "Recurso foi modificado por outro usuário"),

    // ==================== Erros de Validação de Campos (1400-1499) ====================
    FIELD_REQUIRED("ERR-1400", "Campo obrigatório"),
    FIELD_INVALID_FORMAT("ERR-1401", "Formato inválido"),
    FIELD_TOO_SHORT("ERR-1402", "Campo muito curto"),
    FIELD_TOO_LONG("ERR-1403", "Campo muito longo"),
    INVALID_EMAIL("ERR-1404", "Email inválido"),
    INVALID_PASSWORD("ERR-1405", "Senha não atende os requisitos"),
    INVALID_DATE("ERR-1406", "Data inválida"),
    INVALID_DATE_RANGE("ERR-1407", "Intervalo de datas inválido"),
    INVALID_PHONE("ERR-1408", "Telefone inválido"),
    INVALID_CPF("ERR-1409", "CPF inválido"),
    INVALID_CNPJ("ERR-1410", "CNPJ inválido"),
    INVALID_ZIP_CODE("ERR-1411", "CEP inválido"),

    // ==================== Erros de Upload/Download (1500-1599) ====================
    FILE_TOO_LARGE("ERR-1500", "Arquivo muito grande"),
    FILE_TYPE_NOT_ALLOWED("ERR-1501", "Tipo de arquivo não permitido"),
    FILE_UPLOAD_ERROR("ERR-1502", "Erro no upload do arquivo"),
    FILE_DOWNLOAD_ERROR("ERR-1503", "Erro no download do arquivo"),
    FILE_NOT_FOUND("ERR-1504", "Arquivo não encontrado"),
    FILE_CORRUPTED("ERR-1505", "Arquivo corrompido"),
    STORAGE_LIMIT_EXCEEDED("ERR-1506", "Limite de armazenamento excedido"),

    // ==================== Erros de Negócio (1600-1699) ====================
    DEADLINE_EXPIRED("ERR-1600", "Prazo expirado"),
    INSUFFICIENT_PERMISSIONS("ERR-1601", "Permissões insuficientes"),
    OPERATION_NOT_ALLOWED("ERR-1602", "Operação não permitida"),
    ENROLLMENT_CLOSED("ERR-1603", "Período de matrícula encerrado"),
    COURSE_FULL("ERR-1604", "Curso com vagas esgotadas"),
    TASK_ALREADY_SUBMITTED("ERR-1605", "Tarefa já foi enviada"),
    PAYMENT_REQUIRED("ERR-1606", "Pagamento pendente"),
    PAYMENT_FAILED("ERR-1607", "Falha no pagamento"),
    GRADE_ALREADY_ASSIGNED("ERR-1608", "Nota já foi atribuída"),
    CANNOT_DELETE_WITH_DEPENDENCIES("ERR-1609", "Não é possível excluir, existem dependências"),
    INVALID_STATUS_TRANSITION("ERR-1610", "Transição de status inválida"),

    // ==================== Erros de Integração Externa (1700-1799) ====================
    EXTERNAL_SERVICE_ERROR("ERR-1700", "Erro no serviço externo"),
    EMAIL_SEND_ERROR("ERR-1701", "Erro ao enviar email"),
    SMS_SEND_ERROR("ERR-1702", "Erro ao enviar SMS"),
    PAYMENT_GATEWAY_ERROR("ERR-1703", "Erro no gateway de pagamento"),
    ZIP_CODE_API_ERROR("ERR-1704", "Erro ao consultar CEP");

    private final String code;
    private final String defaultMessage;
}