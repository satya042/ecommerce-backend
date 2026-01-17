package com.ecommerce.userservice.service;

import com.ecommerce.userservice.entity.User;
import com.ecommerce.userservice.exception.ResourceNotFoundException;
import com.ecommerce.userservice.exception.UnauthorizedException;
import com.ecommerce.userservice.payload.request.ChangePasswordRequest;
import com.ecommerce.userservice.payload.request.ForgotPasswordRequest;
import com.ecommerce.userservice.payload.request.ResetPasswordRequest;
import com.ecommerce.userservice.payload.request.UpdateUserRequest;
import com.ecommerce.userservice.payload.response.UserResponse;
import com.ecommerce.userservice.repository.RefreshTokenStorageRepository;
import com.ecommerce.userservice.repository.UserRepository;
import com.ecommerce.userservice.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RefreshTokenStorageRepository refreshTokenStorageRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(String token) {
        log.info("Get current user profile");
        String username = jwtUtil.extractUsername(token.substring(7));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: "+ username));
        log.info("Get current user successful: Username={}", username);
        return toUserResponse(user);
    }

    @Override
    public UserResponse updateUser(String token, UpdateUserRequest updateRequest) {
        String username = jwtUtil.extractUsername(token.substring(7));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: "+ username));

        if (updateRequest.getPhone() != null && !updateRequest.getPhone().equals(user.getPhone())) {
            if (userRepository.existsByPhone(updateRequest.getPhone())) {
                throw new RuntimeException("Phone number is already in use!");
            }
            user.setPhone(updateRequest.getPhone());
        }

        // Update other fields
        if (updateRequest.getFullName() != null) {
            user.setFullName(updateRequest.getFullName());
        }

        if (updateRequest.getAvatar() != null) {
            user.setAvatar(updateRequest.getAvatar());
        }

        user = userRepository.save(user);
        log.info("Update user successful: Username={}", username);

        return toUserResponse(user);
    }

    @Override
    public String changePassword(String token, ChangePasswordRequest changePasswordRequest) {
        String username = jwtUtil.extractUsername(token.substring(7));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(changePasswordRequest.getCurrentPassword(), user.getPassword())) {
            throw new UnauthorizedException("Current password is incorrect");
        }

        if (!changePasswordRequest.getNewPassword().equals(changePasswordRequest.getConfirmPassword())) {
            throw new RuntimeException("New password and confirm password do not match");
        }

        user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        userRepository.save(user);
        log.info("Change password successful: Username={}", username);     
        return "Password changed successfully";
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toUserResponse)
                .collect(Collectors.toList());
    }

    @Override
    public String forgotPassword(ForgotPasswordRequest forgotPasswordRequest) {
        User user = userRepository.findByUsername(forgotPasswordRequest.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + forgotPasswordRequest.getEmail()));
         log.info("Forgot password successful: Email={}", forgotPasswordRequest.getEmail());
        // In a real implementation, you would generate a reset token and send it via email
        // For now, we'll just return a success message
        return "Password reset instructions sent to your email";
    }

    @Override
    public String resetPassword(ResetPasswordRequest resetPasswordRequest) {
        // In a real implementation, you would validate the reset token
        // For now, we'll just return a success message
        return "Password reset successfully";
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isTokenBlacklisted(String token) {
        return refreshTokenStorageRepository.existsByRefreshToken(token);
    }

    private UserResponse toUserResponse(User user){
      return UserResponse.builder()
                .fullName(user.getFullName())
                .avatar(user.getAvatar())
                .gender(String.valueOf(user.getGender()))
                .username(user.getUsername())
                .phone(user.getPhone())
                .build();
    }
}

