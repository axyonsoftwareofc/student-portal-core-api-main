package br.com.student.portal.service;

import br.com.student.portal.dto.request.UserRequest;
import br.com.student.portal.dto.response.AuthResponse;
import br.com.student.portal.dto.response.UserResponse;
import br.com.student.portal.entity.UserEntity;
import br.com.student.portal.exception.BadRequestException;
import br.com.student.portal.mapper.UserMapper;
import br.com.student.portal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static br.com.student.portal.validation.UserValidator.validateFieldsUserRequest;


@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    @Transactional
    public UserResponse registerUser(UserRequest userRequest) {
        log.info("Iniciando registro de novo usuário: {}", userRequest.getEmail());

        validateFieldsUserRequest(userRequest);

        UserEntity userEntity = userMapper.userRequestIntoUser(userRequest);
        userEntity.setPassword(passwordEncoder.encode(userRequest.getPassword()));

        UserEntity savedUserEntity = userRepository.save(userEntity);

        log.info("Usuário registrado com sucesso: {} (ID: {})", savedUserEntity.getEmail(), savedUserEntity.getId());
        return userMapper.userIntoUserResponse(savedUserEntity);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(String registration, String password) {
        log.debug("Tentativa de login por matrícula: {}", registration);

        UserEntity userEntity = userRepository.findByRegistration(registration)
                .orElseThrow(() -> {
                    log.warn("Login falhou - matrícula não encontrada: {}", registration);
                    return new BadRequestException(
                            "Matrícula ou senha incorretos.");
                });

        return authenticateUser(userEntity, password);
    }

    @Transactional(readOnly = true)
    public AuthResponse loginByEmail(String email, String password) {
        log.debug("Tentativa de login por email: {}", email);

        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Login falhou - email não encontrado: {}", email);
                    return new BadRequestException(
                            "Email ou senha incorretos.");
                });

        return authenticateUser(userEntity, password);
    }

    private AuthResponse authenticateUser(UserEntity userEntity, String password) {

        String token = tokenService.generateToken(userEntity);

        log.info("Login realizado com sucesso: {} (ID: {})", userEntity.getEmail(), userEntity.getId());

        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .expiresIn(7200L)
                .userId(userEntity.getId())
                .userName(userEntity.getName())
                .userEmail(userEntity.getEmail())
                .userRole(userEntity.getRole().name())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("Buscando usuário por email: {}", email);

        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Tentativa de login com email não cadastrado: {}", email);
                    return new UsernameNotFoundException("Usuário não encontrado com email: " + email);
                });
    }
}