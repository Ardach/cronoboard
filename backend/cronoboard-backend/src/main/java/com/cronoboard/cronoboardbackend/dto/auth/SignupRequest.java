package com.cronoboard.cronoboardbackend.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class SignupRequest {

    @Email(message = "Geçerli bir e-posta giriniz.")
    @NotBlank(message = "E-posta boş olamaz.")
    private String email;

    @NotBlank(message = "Şifre boş olamaz.")
    @Size(min = 8, message = "Şifre en az 8 karakter olmalıdır.")
    private String password;

    @NotBlank(message = "İsim boş olamaz.")
    private String fullName;

    @NotBlank(message = "Kullanıcı adı boş olamaz.")
    @Size(min = 3, max = 60, message = "Kullanıcı adı 3–60 karakter olmalıdır.")
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "Sadece harf, rakam, ., _ ve - kullanılabilir.")
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email == null ? null : email.trim().toLowerCase();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
