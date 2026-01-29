package br.com.student.portal.controller;

import br.com.student.portal.dto.request.UserRequest;
import br.com.student.portal.dto.response.AuthResponse;
import br.com.student.portal.dto.response.UserResponse;
import br.com.student.portal.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody UserRequest userRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authService.registerUser(userRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody UserRequest loginRequest) {
        return ResponseEntity.ok(authService.login(
                loginRequest.getRegistration(),
                loginRequest.getPassword()
        ));
    }
}