package com.ecommerce.userservice.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String accessToken;
    private String tokenType = "Bearer";
    private Long expiresIn = 300L; // 5 minutes in seconds
    
    public AuthResponse(String accessToken) {
        this.accessToken = accessToken;
    }
}