package br.com.student.portal.validation;

import br.com.student.portal.dto.request.UserRequest;
import br.com.student.portal.entity.User;
import br.com.student.portal.exception.ErrorCode;
import br.com.student.portal.exception.types.BadRequestException;
import org.springframework.stereotype.Component;

import static br.com.student.portal.validation.FieldValidator.*;

@Component
public class UserValidator {

    private static final String NAME_FIELD = "Nome";
    private static final String EMAIL_FIELD = "Email";
    private static final String PASSWORD_FIELD = "Senha";

    private static final String NAME_REGEX = "^[a-zA-ZÀ-ÿ\\s]+$";
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    private static final String PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*(),.?\":{}|<>]).{8,}$";

    public static void validateName(String name) {
        validateRequiredField(name, NAME_FIELD);
        validatePattern(name, NAME_FIELD, NAME_REGEX,
                NAME_FIELD + " deve conter apenas letras e espaços.");
    }

    public static void validateEmail(String email) {
        validateRequiredField(email, EMAIL_FIELD);
        validatePattern(email, EMAIL_FIELD, EMAIL_REGEX, "Formato de email inválido.");

        // TODO: Remover essa restrição se quiser aceitar outros emails
        // if (!email.endsWith("@gmail.com")) {
        //     throw new BadRequestException(ErrorCode.INVALID_EMAIL,
        //             "Apenas contas Gmail são permitidas.");
        // }
    }

    public static void validatePassword(String password) {
        validateRequiredField(password, PASSWORD_FIELD);
        validatePattern(password, PASSWORD_FIELD, PASSWORD_REGEX,
                "A senha deve ter no mínimo 8 caracteres, contendo pelo menos uma letra, um número e um caractere especial.");
    }

    public static void validateFieldsUserRequest(UserRequest userRequest) {
        validateRequiredField(userRequest, "UserRequest");
        validateName(userRequest.getName());
        validateEmail(userRequest.getEmail());

        // Senha pode ser opcional em update
        if (isNotEmpty(userRequest.getPassword())) {
            validatePassword(userRequest.getPassword());
        }
    }

    public static void validateFieldsUserRequestForCreate(UserRequest userRequest) {
        validateRequiredField(userRequest, "UserRequest");
        validateName(userRequest.getName());
        validateEmail(userRequest.getEmail());
        validatePassword(userRequest.getPassword()); // Obrigatório na criação
    }

    public static void validateFields(User user) {
        validateRequiredField(user, "User");
        validateName(user.getName());
        validateEmail(user.getEmail());
        validatePassword(user.getPassword());
    }
}