package br.com.student.portal.validation;

import br.com.student.portal.dto.request.UserRequest;
import br.com.student.portal.entity.User;
import br.com.student.portal.exception.ErrorCode;
import br.com.student.portal.exception.types.BadRequestException;
import org.springframework.stereotype.Component;

import static br.com.student.portal.validation.FieldValidator.validateRequiredField;
import static io.micrometer.common.util.StringUtils.isEmpty;
import static java.util.regex.Pattern.matches;

@Component
public class UserValidator {

    private static final String NAME_FIELD = "Nome";
    private static final String EMAIL_FIELD = "Email";
    private static final String PASSWORD_FIELD = "Senha";

    private static final String NAME_REGEX = "^[a-zA-ZÀ-ÿ\\s]+$";
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    private static final String PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*(),.?\":{}|<>]).{8,}$";

    public static void validateName(String name) {
        if (isEmpty(name)) {
            validateRequiredField(name, NAME_FIELD);
        }

        if (!name.matches(NAME_REGEX)) {
            throw new BadRequestException(ErrorCode.FIELD_INVALID_FORMAT,
                    NAME_FIELD + " deve conter apenas letras e espaços.");
        }
    }

    public static void validateEmail(String email) {
        if (isEmpty(email)) {
            validateRequiredField(email, EMAIL_FIELD);
        }

        if (!matches(EMAIL_REGEX, email)) {
            throw new BadRequestException(ErrorCode.INVALID_EMAIL, "Formato de email inválido.");
        }

        // TODO: Considere remover essa validação ou torná-la configurável
        // Restringir apenas a @gmail.com pode ser muito limitante
        if (!email.endsWith("@gmail.com")) {
            throw new BadRequestException(ErrorCode.INVALID_EMAIL,
                    "Apenas contas Gmail são permitidas.");
        }
    }

    public static void validatePassword(String password) {
        if (isEmpty(password)) {
            validateRequiredField(password, PASSWORD_FIELD);
        }

        if (!matches(PASSWORD_REGEX, password)) {
            throw new BadRequestException(ErrorCode.INVALID_PASSWORD,
                    "A senha deve ter no mínimo 8 caracteres, contendo pelo menos uma letra, um número e um caractere especial.");
        }
    }

    public static void validateFieldsUserRequest(UserRequest userRequest) {
        validateName(userRequest.getName());
        validateEmail(userRequest.getEmail());
        validatePassword(userRequest.getPassword());
    }

    public static void validateFields(User user) {
        validateName(user.getName());
        validateEmail(user.getEmail());
        validatePassword(user.getPassword());
    }
}