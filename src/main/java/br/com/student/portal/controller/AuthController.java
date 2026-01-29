package br.com.student.portal.controller;

import br.com.student.portal.dto.request.UserRequest;
import br.com.student.portal.dto.response.AuthResponse;
import br.com.student.portal.dto.response.UserResponse;
import br.com.student.portal.service.auth.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody UserRequest userRequest) {
        UserResponse registeredUser = authService.registerUser(userRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody UserRequest loginRequest) {
        AuthResponse authResponse = authService.login(
                loginRequest.getRegistration(),
                loginRequest.getPassword()
        );
        return ResponseEntity.ok(authResponse);
    }
}