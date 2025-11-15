package com.ecommerce.userservice.service;

import com.ecommerce.userservice.model.entity.User;
import com.ecommerce.userservice.model.request.*;
import com.ecommerce.userservice.model.response.AuthResponse;
import com.ecommerce.userservice.model.response.UserResponse;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    
    // Authentication methods
    AuthResponse register(SignUpRequest signUpRequest);
    AuthResponse login(LoginRequest loginRequest);
    String logout(String token);
    
    // User management methods
    UserResponse getCurrentUser(String username);
    UserResponse updateUser(String username, UpdateUserRequest updateRequest);
    String changePassword(String username, ChangePasswordRequest changePasswordRequest);
    String deleteUser(String username);
    
    // Admin methods
    List<UserResponse> getAllUsers();
    UserResponse getUserById(Long userId);
    UserResponse updateUserById(Long userId, UpdateUserRequest updateRequest);
    String deleteUserById(Long userId);
    String enableUser(Long userId);
    String disableUser(Long userId);
    
    // Password reset methods
    String forgotPassword(ForgotPasswordRequest forgotPasswordRequest);
    String resetPassword(ResetPasswordRequest resetPasswordRequest);
    
    // Utility methods
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
}
