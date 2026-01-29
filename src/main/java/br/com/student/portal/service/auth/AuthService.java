package br.com.student.portal.service.auth;

import br.com.student.portal.dto.request.UserRequest;
import br.com.student.portal.dto.response.AuthResponse;
import br.com.student.portal.dto.response.UserResponse;
import br.com.student.portal.entity.User;
import br.com.student.portal.exception.ErrorCode;
import br.com.student.portal.exception.types.ConflictException;
import br.com.student.portal.exception.types.UnauthorizedException;
import br.com.student.portal.mapper.UserMapper;
import br.com.student.portal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static br.com.student.portal.validation.UserValidator.validateFieldsUserRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    /**
     * Registra um novo usuário no sistema.
     */
    @Transactional
    public UserResponse registerUser(UserRequest userRequest) {
        log.info("Iniciando registro de novo usuário: {}", userRequest.getEmail());

        validateFieldsUserRequest(userRequest);
        checkEmailAvailability(userRequest.getEmail());
        checkRegistrationAvailability(userRequest.getRegistration());

        User user = userMapper.userRequestIntoUser(userRequest);
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));

        User savedUser = userRepository.save(user);

        log.info("Usuário registrado com sucesso: {} (ID: {})", savedUser.getEmail(), savedUser.getId());
        return userMapper.userIntoUserResponse(savedUser);
    }

    /**
     * Autentica usuário por matrícula e senha.
     */
    @Transactional(readOnly = true)
    public AuthResponse login(String registration, String password) {
        log.debug("Tentativa de login por matrícula: {}", registration);

        User user = userRepository.findByRegistration(registration)
                .orElseThrow(() -> {
                    log.warn("Login falhou - matrícula não encontrada: {}", registration);
                    return new UnauthorizedException(ErrorCode.INVALID_CREDENTIALS,
                            "Matrícula ou senha incorretos.");
                });

        return authenticateUser(user, password);
    }

    /**
     * Autentica usuário por email e senha.
     */
    @Transactional(readOnly = true)
    public AuthResponse loginByEmail(String email, String password) {
        log.debug("Tentativa de login por email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Login falhou - email não encontrado: {}", email);
                    return new UnauthorizedException(ErrorCode.INVALID_CREDENTIALS,
                            "Email ou senha incorretos.");
                });

        return authenticateUser(user, password);
    }

    // ==================== Métodos Privados ====================

    private AuthResponse authenticateUser(User user, String password) {
        validateUserAccess(user);
        validatePassword(user, password);

        String token = tokenService.generateToken(user);

        log.info("Login realizado com sucesso: {} (ID: {})", user.getEmail(), user.getId());

        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .expiresIn(7200L)
                .userId(user.getId())
                .userName(user.getName())
                .userEmail(user.getEmail())
                .userRole(user.getRole().name())
                .build();
    }

    private void validateUserAccess(User user) {
        // ✅ Corrigido: usa getAccessEnable() que é o getter gerado pelo Lombok
        if (user.getAccessEnable() == null || !user.getAccessEnable()) {
            log.warn("Tentativa de login com conta desativada: {}", user.getEmail());
            throw new UnauthorizedException(ErrorCode.ACCOUNT_DISABLED,
                    "Sua conta está desativada. Entre em contato com o suporte.");
        }
    }

    private void validatePassword(User user, String password) {
        if (!passwordEncoder.matches(password, user.getPassword())) {
            log.warn("Login falhou - senha incorreta para: {}", user.getEmail());
            throw new UnauthorizedException(ErrorCode.INVALID_CREDENTIALS,
                    "Credenciais inválidas.");
        }
    }

    private void checkEmailAvailability(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            log.warn("Tentativa de registro com email já existente: {}", email);
            throw new ConflictException(ErrorCode.EMAIL_ALREADY_EXISTS,
                    "O email " + email + " já está cadastrado.");
        }
    }

    private void checkRegistrationAvailability(String registration) {
        if (registration != null && userRepository.findByRegistration(registration).isPresent()) {
            log.warn("Tentativa de registro com matrícula já existente: {}", registration);
            throw new ConflictException(ErrorCode.REGISTRATION_ALREADY_EXISTS,
                    "A matrícula " + registration + " já está cadastrada.");
        }
    }
}