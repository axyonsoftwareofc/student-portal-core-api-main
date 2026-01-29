package br.com.student.portal.service;

import br.com.student.portal.dto.request.UserRequest;
import br.com.student.portal.dto.response.AuthResponse;
import br.com.student.portal.dto.response.UserResponse;
import br.com.student.portal.entity.User;
import br.com.student.portal.exception.BadRequestException;
import br.com.student.portal.exception.ForbiddenException;
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

    @Transactional(readOnly = true)
    public AuthResponse login(String registration, String password) {
        log.debug("Tentativa de login por matrícula: {}", registration);

        User user = userRepository.findByRegistration(registration)
                .orElseThrow(() -> {
                    log.warn("Login falhou - matrícula não encontrada: {}", registration);
                    return new BadRequestException(
                            "Matrícula ou senha incorretos.");
                });

        return authenticateUser(user, password);
    }

    @Transactional(readOnly = true)
    public AuthResponse loginByEmail(String email, String password) {
        log.debug("Tentativa de login por email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Login falhou - email não encontrado: {}", email);
                    return new BadRequestException(
                            "Email ou senha incorretos.");
                });

        return authenticateUser(user, password);
    }

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
        if (user.getAccessEnable() == null || !user.getAccessEnable()) {
            log.warn("Tentativa de login com conta desativada: {}", user.getEmail());
            throw new ForbiddenException("Sua conta está desativada. Entre em contato com o suporte.");
        }
    }

    private void validatePassword(User user, String password) {
        if (!passwordEncoder.matches(password, user.getPassword())) {
            log.warn("Login falhou - senha incorreta para: {}", user.getEmail());
            throw new BadRequestException("Credenciais inválidas.");
        }
    }

    private void checkEmailAvailability(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            log.warn("Tentativa de registro com email já existente: {}", email);
            throw new BadRequestException("O email " + email + " já está cadastrado.");
        }
    }

    private void checkRegistrationAvailability(String registration) {
        if (registration != null && userRepository.findByRegistration(registration).isPresent()) {
            log.warn("Tentativa de registro com matrícula já existente: {}", registration);
            throw new BadRequestException("A matrícula " + registration + " já está cadastrada.");
        }
    }
}