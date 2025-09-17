package com.cronoboard.cronoboardbackend.service;

import com.cronoboard.cronoboardbackend.dto.auth.LoginRequest;
import com.cronoboard.cronoboardbackend.dto.auth.SignupRequest;
import com.cronoboard.cronoboardbackend.entity.User;
import com.cronoboard.cronoboardbackend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User register(SignupRequest req) {
        userRepository.findByEmailIgnoreCase(req.getEmail()).ifPresent(u -> {
            throw new IllegalArgumentException("Bu e-posta zaten kayıtlı.");
        });
        userRepository.findByUsernameIgnoreCase(req.getUsername()).ifPresent(u -> {
            throw new IllegalArgumentException("Bu kullanıcı adı zaten alınmış.");
        });

        User u = new User();
        u.setEmail(req.getEmail());                         // setter lower-case yapmalı
        u.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        u.setUsername(req.getUsername());                   // setter lower-case yapmalı
        u.setCreatedAt(OffsetDateTime.now());
        u.setUpdatedAt(OffsetDateTime.now());
        if (req.getFullName() != null && !req.getFullName().isBlank()) {
            u.setFullName(req.getFullName().trim());
        }

        return userRepository.save(u);
    }

    @Transactional(readOnly = true)
    public User authenticate(LoginRequest req) {
        User user = userRepository.findByEmailIgnoreCase(req.getEmail())
            .orElseThrow(() -> new IllegalArgumentException("E-posta veya şifre hatalı."));
        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("E-posta veya şifre hatalı.");
        }
        return user;
    }
}
