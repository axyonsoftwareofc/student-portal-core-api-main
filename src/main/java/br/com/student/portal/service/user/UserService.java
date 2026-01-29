package br.com.student.portal.service.user;

import br.com.student.portal.dto.request.UserRequest;
import br.com.student.portal.dto.response.UserResponse;
import br.com.student.portal.entity.User;
import br.com.student.portal.exception.ErrorCode;
import br.com.student.portal.exception.types.ConflictException;
import br.com.student.portal.exception.types.NotFoundException;
import br.com.student.portal.mapper.UserMapper;
import br.com.student.portal.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static br.com.student.portal.validation.UserValidator.validateFieldsUserRequest;

@Slf4j
@AllArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserResponse createUser(UserRequest userRequest) {
        log.info("Criando novo usuário: {}", userRequest.getEmail());

        validateFieldsUserRequest(userRequest);

        // Verificar email duplicado
        if (userRepository.findByEmail(userRequest.getEmail()).isPresent()) {
            throw new ConflictException(ErrorCode.EMAIL_ALREADY_EXISTS,
                    "O email " + userRequest.getEmail() + " já está cadastrado.");
        }

        // Verificar matrícula duplicada
        if (userRequest.getRegistration() != null &&
                userRepository.findByRegistration(userRequest.getRegistration()).isPresent()) {
            throw new ConflictException(ErrorCode.REGISTRATION_ALREADY_EXISTS,
                    "A matrícula " + userRequest.getRegistration() + " já está cadastrada.");
        }

        User user = userMapper.userRequestIntoUser(userRequest);
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));

        User savedUser = userRepository.save(user);
        log.info("Usuário criado com ID: {}", savedUser.getId());

        return userMapper.userIntoUserResponse(savedUser);
    }

    public UserResponse getUserById(UUID id) {
        log.debug("Buscando usuário por ID: {}", id);
        User user = findUserById(id);
        return userMapper.toResponse(user);
    }

    public List<UserResponse> getAllUsers() {
        log.debug("Buscando todos os usuários");
        var users = userRepository.findAll();

        if (users.isEmpty()) {
            throw new NotFoundException(ErrorCode.USER_NOT_FOUND, "Nenhum usuário encontrado.");
        }

        return users.stream()
                .map(userMapper::userIntoUserResponse)
                .collect(Collectors.toList());
    }

    public UserResponse updateUser(UUID id, UserRequest userRequest) {
        log.info("Atualizando usuário ID: {}", id);

        var user = findUserById(id);
        validateFieldsUserRequest(userRequest);

        // Verificar se o novo email já está em uso por outro usuário
        userRepository.findByEmail(userRequest.getEmail())
                .filter(existingUser -> !existingUser.getId().equals(id))
                .ifPresent(existingUser -> {
                    throw new ConflictException(ErrorCode.EMAIL_ALREADY_EXISTS,
                            "O email " + userRequest.getEmail() + " já está em uso.");
                });

        user.setName(userRequest.getName());
        user.setEmail(userRequest.getEmail());

        if (userRequest.getPassword() != null && !userRequest.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        }

        User updatedUser = userRepository.save(user);
        log.info("Usuário atualizado: {}", updatedUser.getId());

        return userMapper.userIntoUserResponse(updatedUser);
    }

    public void deleteUser(UUID id) {
        log.info("Deletando usuário ID: {}", id);
        var user = findUserById(id);
        userRepository.delete(user);
        log.info("Usuário deletado: {}", id);
    }

    public UserResponse getUserByRegistration(String registration) {
        log.debug("Buscando usuário por matrícula: {}", registration);
        User user = userRepository.findByRegistration(registration)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND,
                        "Usuário com matrícula " + registration + " não encontrado."));
        return userMapper.userIntoUserResponse(user);
    }

    private User findUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND,
                        "Usuário não encontrado com ID: " + id));
    }
}