package com.ecommerce.userservice.service.impl;

import com.ecommerce.userservice.exception.ResourceNotFoundException;
import com.ecommerce.userservice.exception.UnauthorizedException;
import com.ecommerce.userservice.model.entity.User;
import com.ecommerce.userservice.model.request.*;
import com.ecommerce.userservice.model.response.AuthResponse;
import com.ecommerce.userservice.model.response.UserResponse;
import com.ecommerce.userservice.repository.UserRepository;
import com.ecommerce.userservice.service.UserService;
import com.ecommerce.userservice.util.JwtUtil;
import com.ecommerce.userservice.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordUtil passwordUtil;
    private final JwtUtil jwtUtil;

    @Override
    public AuthResponse register(SignUpRequest signUpRequest) {
        // Check if user already exists
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            throw new RuntimeException("Username is already taken!");
        }
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new RuntimeException("Email is already in use!");
        }
        if (signUpRequest.getPhone() != null && userRepository.existsByPhone(signUpRequest.getPhone())) {
            throw new RuntimeException("Phone number is already in use!");
        }

        // Create new user
        User user = new User();
        user.setFullname(signUpRequest.getFullname());
        user.setUsername(signUpRequest.getUsername());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(passwordUtil.encodePassword(signUpRequest.getPassword()));
        user.setGender(User.Gender.valueOf(signUpRequest.getGender()));
        user.setAvatar(signUpRequest.getAvatar());
        user.setPhone(signUpRequest.getPhone());
        user.setRole(User.Role.USER);

        User savedUser = userRepository.save(user);

        // Generate JWT token
        String token = jwtUtil.generateToken(savedUser);
        
        return new AuthResponse(
            token,
            savedUser.getUsername(),
            savedUser.getEmail(),
            savedUser.getRole().name(),
            savedUser.getId(),
            jwtUtil.getExpirationTime()
        );
    }

    @Override
    public AuthResponse login(LoginRequest loginRequest) {
        User user = userRepository.findActiveUserByUsernameOrEmail(loginRequest.getUsernameOrEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!passwordUtil.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid password");
        }

        String token = jwtUtil.generateToken(user);
        
        return new AuthResponse(
            token,
            user.getUsername(),
            user.getEmail(),
            user.getRole().name(),
            user.getId(),
            jwtUtil.getExpirationTime()
        );
    }

    @Override
    public String logout(String token) {
        // In a stateless JWT implementation, logout is typically handled client-side
        // by removing the token from storage. For server-side logout, you could
        // maintain a blacklist of tokens, but that's not implemented here.
        return "Logged out successfully";
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return UserResponse.fromUser(user);
    }

    @Override
    public UserResponse updateUser(String username, UpdateUserRequest updateRequest) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Check for unique constraints
        if (updateRequest.getUsername() != null && !updateRequest.getUsername().equals(username)) {
            if (userRepository.existsByUsername(updateRequest.getUsername())) {
                throw new RuntimeException("Username is already taken!");
            }
            user.setUsername(updateRequest.getUsername());
        }

        if (updateRequest.getEmail() != null && !updateRequest.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(updateRequest.getEmail())) {
                throw new RuntimeException("Email is already in use!");
            }
            user.setEmail(updateRequest.getEmail());
        }

        if (updateRequest.getPhone() != null && !updateRequest.getPhone().equals(user.getPhone())) {
            if (userRepository.existsByPhone(updateRequest.getPhone())) {
                throw new RuntimeException("Phone number is already in use!");
            }
            user.setPhone(updateRequest.getPhone());
        }

        // Update other fields
        if (updateRequest.getFullname() != null) {
            user.setFullname(updateRequest.getFullname());
        }
        if (updateRequest.getGender() != null) {
            user.setGender(User.Gender.valueOf(updateRequest.getGender()));
        }
        if (updateRequest.getAvatar() != null) {
            user.setAvatar(updateRequest.getAvatar());
        }

        User updatedUser = userRepository.save(user);
        return UserResponse.fromUser(updatedUser);
    }

    @Override
    public String changePassword(String username, ChangePasswordRequest changePasswordRequest) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordUtil.matches(changePasswordRequest.getCurrentPassword(), user.getPassword())) {
            throw new UnauthorizedException("Current password is incorrect");
        }

        if (!changePasswordRequest.getNewPassword().equals(changePasswordRequest.getConfirmPassword())) {
            throw new RuntimeException("New password and confirm password do not match");
        }

        user.setPassword(passwordUtil.encodePassword(changePasswordRequest.getNewPassword()));
        userRepository.save(user);

        return "Password changed successfully";
    }

    @Override
    public String deleteUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        userRepository.delete(user);
        return "User deleted successfully";
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserResponse::fromUser)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        return UserResponse.fromUser(user);
    }

    @Override
    public UserResponse updateUserById(Long userId, UpdateUserRequest updateRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Similar logic to updateUser but for admin use
        if (updateRequest.getUsername() != null && !updateRequest.getUsername().equals(user.getUsername())) {
            if (userRepository.existsByUsername(updateRequest.getUsername())) {
                throw new RuntimeException("Username is already taken!");
            }
            user.setUsername(updateRequest.getUsername());
        }

        if (updateRequest.getEmail() != null && !updateRequest.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(updateRequest.getEmail())) {
                throw new RuntimeException("Email is already in use!");
            }
            user.setEmail(updateRequest.getEmail());
        }

        if (updateRequest.getPhone() != null && !updateRequest.getPhone().equals(user.getPhone())) {
            if (userRepository.existsByPhone(updateRequest.getPhone())) {
                throw new RuntimeException("Phone number is already in use!");
            }
            user.setPhone(updateRequest.getPhone());
        }

        if (updateRequest.getFullname() != null) {
            user.setFullname(updateRequest.getFullname());
        }
        if (updateRequest.getGender() != null) {
            user.setGender(User.Gender.valueOf(updateRequest.getGender()));
        }
        if (updateRequest.getAvatar() != null) {
            user.setAvatar(updateRequest.getAvatar());
        }

        User updatedUser = userRepository.save(user);
        return UserResponse.fromUser(updatedUser);
    }

    @Override
    public String deleteUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        userRepository.delete(user);
        return "User deleted successfully";
    }

    @Override
    public String enableUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        user.setIsEnabled(true);
        userRepository.save(user);
        return "User enabled successfully";
    }

    @Override
    public String disableUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        user.setIsEnabled(false);
        userRepository.save(user);
        return "User disabled successfully";
    }

    @Override
    public String forgotPassword(ForgotPasswordRequest forgotPasswordRequest) {
        User user = userRepository.findByEmail(forgotPasswordRequest.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + forgotPasswordRequest.getEmail()));
        
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
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByPhone(String phone) {
        return userRepository.existsByPhone(phone);
    }
}
