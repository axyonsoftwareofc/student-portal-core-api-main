package br.com.student.portal.service;

import br.com.student.portal.dto.request.UserRequest;
import br.com.student.portal.entity.UserEntity;
import br.com.student.portal.mapper.UserMapper;
import br.com.student.portal.repository.UserRepository;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.BDDMockito.given;

@ExtendWith(SpringExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserService userService;

    //TODO:FINALIZAR OS TESTES DE USUÁRIO E CRIAR TESTES PARA AS OUTRAS CLASSES

    @Test
    public void mustCreateUser(){
        var userRequest = new UserRequest();
        userRequest.setEmail("code@gmail.com");
        userRequest.setName("Code");
        userRequest.setPassword("12345");
        userRequest.setRole("ADMIN");
        userRequest.setRegistration("1234");

        var userEntity = new UserEntity();

        given(userMapper.userRequestIntoUser(userRequest)).willReturn(userEntity);

        var result = userService.createUser(userRequest);

    }
}
