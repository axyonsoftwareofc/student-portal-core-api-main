package br.com.student.portal.validation;

import br.com.student.portal.exception.ErrorCode;
import br.com.student.portal.exception.types.BadRequestException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

import static br.com.student.portal.validation.FieldValidator.*;

@Component
public class MaterialValidator {

    private static final String NAME_FIELD = "Nome";
    private static final String DESCRIPTION_FIELD = "Descrição";
    private static final String CATEGORY_FIELD = "Categoria";
    private static final String FILE_FIELD = "Arquivo";

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "application/vnd.ms-powerpoint",
            "application/vnd.openxmlformats-officedocument.presentationml.presentation",
            "image/jpeg",
            "image/png",
            "image/gif",
            "video/mp4",
            "audio/mpeg",
            "application/zip",
            "text/plain"
    );

    public static void validateMaterialRequest(MaterialRequest request) {
        validateRequiredField(request, "MaterialRequest");
        validateRequiredField(request.getName(), NAME_FIELD);
        validateMinLength(request.getName(), NAME_FIELD, 3);
        validateMaxLength(request.getName(), NAME_FIELD, 255);
        validateRequiredField(request.getDescription(), DESCRIPTION_FIELD);
        validateRequiredField(request.getCategory(), CATEGORY_FIELD);
    }

    public static void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException(ErrorCode.FIELD_REQUIRED, FILE_FIELD + " é obrigatório.");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BadRequestException(ErrorCode.FILE_TOO_LARGE,
                    "O arquivo não pode exceder 10MB.");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new BadRequestException(ErrorCode.FILE_TYPE_NOT_ALLOWED,
                    "Tipo de arquivo não permitido: " + contentType);
        }
    }
}