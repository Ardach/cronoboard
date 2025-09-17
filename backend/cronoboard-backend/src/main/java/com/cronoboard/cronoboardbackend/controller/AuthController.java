package com.cronoboard.cronoboardbackend.controller;

import com.cronoboard.cronoboardbackend.dto.auth.*;
import com.cronoboard.cronoboardbackend.entity.User;
import com.cronoboard.cronoboardbackend.security.JwtService;
import com.cronoboard.cronoboardbackend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    public AuthController(AuthService authService, JwtService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }

    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(@Valid @RequestBody SignupRequest request) {
        User created = authService.register(request);
        return ResponseEntity.ok(new SignupResponse(
            created.getId(),
            created.getUsername(),
            created.getEmail(),
            created.getFullName()
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        User user = authService.authenticate(request);
        String token = jwtService.generateToken(user.getEmail(), user.getId());
        long expiresIn = jwtService.getDefaultExpirySeconds();
        return ResponseEntity.ok(new LoginResponse(
            token, expiresIn, user.getId(), user.getUsername()
        ));
    }
}
