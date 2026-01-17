package com.ecommerce.userservice.service;

import com.ecommerce.userservice.payload.request.LoginRequest;
import com.ecommerce.userservice.payload.request.SignUpRequest;

public interface AuthService {
    // Authentication methods
    String[] register(SignUpRequest signUpRequest);
    String[] login(LoginRequest loginRequest);
    String[] refreshToken(String refreshToken);
    String logout(String token);
}
