package com.cronoboard.cronoboardbackend.dto.auth;

public class LoginResponse {
    private String token;
    private long expiresIn;   // saniye cinsinden (ör. 3600 = 1 saat)
    private Long userId;
    private String username;

    public LoginResponse(String token, long expiresIn, Long userId, String username) {
        this.token = token;
        this.expiresIn = expiresIn;
        this.userId = userId;
        this.username = username;
    }

    public String getToken() { return token; }
    public long getExpiresIn() { return expiresIn; }
    public Long getUserId() { return userId; }
    public String getUsername() { return username; }
}
