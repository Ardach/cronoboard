package com.cronoboard.cronoboardbackend.dto.auth;

public class SignupResponse {
    private final Long userId;
    private final String username;
    private final String email;
    private final String fullName;

    public SignupResponse(Long userId, String username, String email, String fullName) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.fullName = fullName;
    }

    public Long getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getFullName() { return fullName; }
}
