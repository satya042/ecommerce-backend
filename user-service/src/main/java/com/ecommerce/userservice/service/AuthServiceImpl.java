package com.ecommerce.userservice.service;

import com.ecommerce.userservice.entity.RefreshTokenStorage;
import com.ecommerce.userservice.entity.User;
import com.ecommerce.userservice.entity.enums.Gender;
import com.ecommerce.userservice.entity.enums.Role;
import com.ecommerce.userservice.exception.ResourceNotFoundException;
import com.ecommerce.userservice.exception.UnauthorizedException;
import com.ecommerce.userservice.payload.request.LoginRequest;
import com.ecommerce.userservice.payload.request.SignUpRequest;
import com.ecommerce.userservice.repository.RefreshTokenStorageRepository;
import com.ecommerce.userservice.repository.UserRepository;
import com.ecommerce.userservice.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService{

    private final UserRepository userRepository;
    private final RefreshTokenStorageRepository refreshTokenStorageRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    @Transactional
    public String[] register(SignUpRequest signUpRequest) {
        log.info("Register: Username={}", signUpRequest.getUsername());
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            throw new RuntimeException("Email is already in use!");
        }
        if (signUpRequest.getPhone() != null && userRepository.existsByPhone(signUpRequest.getPhone())) {
            throw new RuntimeException("Phone number is already in use!");
        }

        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        user.setGender(Gender.valueOf(signUpRequest.getGender()));
        user.setAvatar(signUpRequest.getAvatar());
        user.setPhone(signUpRequest.getPhone());
        user.setFullName(signUpRequest.getFullName());
        user.setRole(Role.ADMIN);

        user = userRepository.save(user);

        String[] tokens = new String[2];
        tokens[0] = jwtUtil.generateAccessToken(user);
        tokens[1] = jwtUtil.generateRefreshToken(user.getUsername());

        saveRefreshToken(tokens[1], user.getUsername());

        return tokens;
    }

    @Override
    @Transactional
    public String[] login(LoginRequest loginRequest) {
        log.info("Entering login method with username: " + loginRequest.getUsername() + "password: " + loginRequest.getPassword());
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(), loginRequest.getPassword()));

        User user = (User) authentication.getPrincipal();

        String[] tokens = new String[2];
        tokens[0] = jwtUtil.generateAccessToken(user);
        tokens[1] = jwtUtil.generateRefreshToken(user.getUsername());

        saveRefreshToken(tokens[1], user.getUsername());
        return tokens;
    }

    @Override
    @Transactional
    public String[] refreshToken(String refreshToken) {
        // Validate refresh token signature
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new UnauthorizedException("Invalid or expired refresh token");
        }

        // Check if refresh token exists and is not revoked in DB
        RefreshTokenStorage tokenStorage = refreshTokenStorageRepository.findActiveRefreshToken(refreshToken)
                .orElseThrow(() -> new UnauthorizedException("Refresh token not found or has been revoked"));

        String username = jwtUtil.extractUsername(refreshToken);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));


        // Revoke the old refresh token
        tokenStorage.setIsRevoked(true);
        refreshTokenStorageRepository.save(tokenStorage);

        String[] tokens = new String[2];
        tokens[0] = jwtUtil.generateAccessToken(user);
        tokens[1] = jwtUtil.generateRefreshToken(user.getUsername());

        saveRefreshToken(tokens[1], user.getUsername());
        return tokens;
    }

    @Override
    @Transactional
    public String logout(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        String username = jwtUtil.extractUsername(token);

        // Blacklist all active refresh tokens for this user
        refreshTokenStorageRepository.revokeAllTokensByUsername(username);
        log.info("Logged out successfully");
        return "Logged out successfully";
    }

    private void saveRefreshToken(String refreshToken, String username) {
        try {
            Date expirationDate = jwtUtil.extractExpiration(refreshToken);
            LocalDateTime expiresAt = new java.sql.Timestamp(expirationDate.getTime()).toLocalDateTime();

            RefreshTokenStorage tokenStorage = new RefreshTokenStorage();
            tokenStorage.setRefreshToken(refreshToken);
            tokenStorage.setUsername(username);
            tokenStorage.setExpiresAt(expiresAt);
            tokenStorage.setIsRevoked(false);

            refreshTokenStorageRepository.save(tokenStorage);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save refresh token: " + e.getMessage());
        }
    }
}
