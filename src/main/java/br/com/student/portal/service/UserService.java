package br.com.student.portal.service;

import br.com.student.portal.dto.request.UserRequest;
import br.com.student.portal.dto.response.UserResponse;
import br.com.student.portal.entity.UserEntity;
import br.com.student.portal.exception.BadRequestException;
import br.com.student.portal.exception.ObjectNotFoundException;
import br.com.student.portal.mapper.UserMapper;
import br.com.student.portal.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static br.com.student.portal.validation.UserValidator.validateFieldsUserRequest;

@AllArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserResponse createUser(UserRequest userRequest) {

        validateFieldsUserRequest(userRequest);
        findUserByEmail(userRequest);
        findUserByRegistration(userRequest);

        UserEntity userEntity = userMapper.userRequestIntoUser(userRequest);
        userEntity.setPassword(passwordEncoder.encode(userRequest.getPassword()));

        UserEntity savedUserEntity = userRepository.save(userEntity);

        return userMapper.userIntoUserResponse(savedUserEntity);
    }

    public UserResponse getUserById(UUID id) {
        var userEntity = findUserById(id);
        return userMapper.toResponse(userEntity);
    }

    public List<UserResponse> getAllUsers() {
        var users = userRepository.findAll();

        if (users.isEmpty()) {
            throw new ObjectNotFoundException("Nenhum usuário encontrado.");
        }

        return users.stream()
                .map(userMapper::userIntoUserResponse)
                .collect(Collectors.toList());
    }

    public UserResponse updateUser(UUID id, UserRequest userRequest) {
        var user = findUserById(id);

        validateFieldsUserRequest(userRequest);
        findUserByEmail(userRequest);

        user.setName(userRequest.getName());
        user.setEmail(userRequest.getEmail());

        if (userRequest.getPassword() != null && !userRequest.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        }

        return userMapper.userIntoUserResponse(userRepository.save(user));
    }

    public void deleteUser(UUID id) {
        userRepository.delete(findUserById(id));
    }

    public UserResponse getUserByRegistration(String registration) {
        var userEntity = userRepository.findByRegistration(registration)
                .orElseThrow(() -> new ObjectNotFoundException(
                        "Usuário com matrícula " + registration + " não encontrado."));

        return userMapper.userIntoUserResponse(userEntity);
    }

    private UserEntity findUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException(
                        "Usuário não encontrado com ID: " + id));
    }

    private void findUserByEmail(UserRequest userRequest) {
        if (userRepository.findByEmail(userRequest.getEmail()).isPresent()) {
            throw new BadRequestException(
                    "O email " + userRequest.getEmail() + " já está cadastrado.");
        }
    }

    private void findUserByRegistration(UserRequest userRequest) {
        if (userRequest.getRegistration() != null &&
                userRepository.findByRegistration(userRequest.getRegistration()).isPresent()) {
            throw new BadRequestException(
                    "A matrícula " + userRequest.getRegistration() + " já está cadastrada.");
        }
    }
}