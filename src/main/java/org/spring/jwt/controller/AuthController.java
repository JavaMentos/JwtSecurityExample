package org.spring.jwt.controller;

import lombok.RequiredArgsConstructor;
import org.spring.jwt.dto.AuthRequest;
import org.spring.jwt.dto.AuthResponse;
import org.spring.jwt.dto.RegistrationRequest;
import org.spring.jwt.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

    @PostMapping("/registration")
    public ResponseEntity<String> register(@RequestBody RegistrationRequest registrationRequest) {
        userService.register(registrationRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest authRequest) {
        return ResponseEntity.ok(userService.auth(authRequest));
    }
}
