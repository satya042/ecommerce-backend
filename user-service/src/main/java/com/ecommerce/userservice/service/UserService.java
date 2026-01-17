package com.ecommerce.userservice.service;

import com.ecommerce.userservice.payload.request.*;
import com.ecommerce.userservice.payload.response.UserResponse;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {

    // User management methods
    UserResponse getCurrentUser(String token);
    UserResponse updateUser(String token, UpdateUserRequest updateRequest);
    String changePassword(String token, ChangePasswordRequest changePasswordRequest);

    // Admin methods
    List<UserResponse> getAllUsers();

    // Password reset methods
    String forgotPassword(ForgotPasswordRequest forgotPasswordRequest);
    String resetPassword(ResetPasswordRequest resetPasswordRequest);
    
    // Utility methods
    boolean isTokenBlacklisted(String token);
}
