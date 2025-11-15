package com.ecommerce.userservice.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String type = "Bearer";
    private String username;
    private String email;
    private String role;
    private Long userId;
    private long expiresIn;

    public AuthResponse(String token, String username, String email, String role, Long userId, long expiresIn) {
        this.token = token;
        this.username = username;
        this.email = email;
        this.role = role;
        this.userId = userId;
        this.expiresIn = expiresIn;
    }
}
