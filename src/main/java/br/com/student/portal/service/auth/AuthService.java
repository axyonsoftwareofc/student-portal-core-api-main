package br.com.student.portal.service.auth;

import br.com.student.portal.dto.request.UserRequest;
import br.com.student.portal.dto.response.UserResponse;
import br.com.student.portal.entity.User;
import br.com.student.portal.exception.ErrorCode;
import br.com.student.portal.exception.types.ConflictException;
import br.com.student.portal.exception.types.NotFoundException;
import br.com.student.portal.exception.types.UnauthorizedException;
import br.com.student.portal.mapper.UserMapper;
import br.com.student.portal.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static br.com.student.portal.validation.UserValidator.validateFieldsUserRequest;

@AllArgsConstructor
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserResponse registerUser(UserRequest userRequest) {
        validateFieldsUserRequest(userRequest);

        // Verificar se email já existe
        if (userRepository.findByEmail(userRequest.getEmail()).isPresent()) {
            throw new ConflictException(ErrorCode.EMAIL_ALREADY_EXISTS,
                    "O email " + userRequest.getEmail() + " já está cadastrado.");
        }

        // Verificar se matrícula já existe
        if (userRequest.getRegistration() != null &&
                userRepository.findByRegistration(userRequest.getRegistration()).isPresent()) {
            throw new ConflictException(ErrorCode.REGISTRATION_ALREADY_EXISTS,
                    "A matrícula " + userRequest.getRegistration() + " já está cadastrada.");
        }

        User user = userMapper.userRequestIntoUser(userRequest);
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));

        User savedUser = userRepository.save(user);
        return userMapper.userIntoUserResponse(savedUser);
    }

    public UserResponse login(String registration, String password) {
        User user = userRepository.findByRegistration(registration)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND,
                        "Usuário com matrícula " + registration + " não encontrado."));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new UnauthorizedException(ErrorCode.INVALID_CREDENTIALS,
                    "Matrícula ou senha incorretos.");
        }

        return userMapper.userIntoUserResponse(user);
    }
}