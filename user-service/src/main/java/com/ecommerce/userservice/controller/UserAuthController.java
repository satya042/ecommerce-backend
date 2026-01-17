package com.ecommerce.userservice.controller;

import com.ecommerce.userservice.advice.ApiResponse;
import com.ecommerce.userservice.payload.request.LoginRequest;
import com.ecommerce.userservice.payload.request.RefreshTokenRequest;
import com.ecommerce.userservice.payload.request.SignUpRequest;
import com.ecommerce.userservice.payload.response.AuthResponse;
import com.ecommerce.userservice.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserAuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody SignUpRequest signUpRequest, HttpServletResponse httpServletResponse) {
        String[] tokens = authService.register(signUpRequest);
        
        // Set refresh token as HTTP-only cookie
        Cookie cookie = new Cookie("refreshToken", tokens[1]);
        cookie.setHttpOnly(true);
//        cookie.setSecure(true); // Use only over HTTPS in production
        cookie.setPath("/");
        cookie.setMaxAge(30 * 24 * 60 * 60); // 30 days
        httpServletResponse.addCookie(cookie);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(
            new AuthResponse(tokens[0], "Bearer", 300L)
        );
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse httpServletResponse) {
        String[] tokens = authService.login(loginRequest);
        
        // Set refresh token as HTTP-only cookie
        Cookie cookie = new Cookie("refreshToken", tokens[1]);
        cookie.setHttpOnly(true);
//        cookie.setSecure(true); // Use only over HTTPS in production
        cookie.setPath("/");
        cookie.setMaxAge(30 * 24 * 60 * 60); // 30 days
        httpServletResponse.addCookie(cookie);
        
        return ResponseEntity.ok(new AuthResponse(tokens[0], "Bearer", 300L));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest, HttpServletResponse httpServletResponse) {
        try {
            String[] tokens = authService.refreshToken(refreshTokenRequest.getRefreshToken());
            
            // Update refresh token cookie
            Cookie cookie = new Cookie("refreshToken", tokens[1]);
            cookie.setHttpOnly(true);
//            cookie.setSecure(true); // Use only over HTTPS in production
            cookie.setPath("/");
            cookie.setMaxAge(30 * 24 * 60 * 60); // 30 days
            httpServletResponse.addCookie(cookie);
            
            return ResponseEntity.ok(new AuthResponse(tokens[0], "Bearer", 300L));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new AuthResponse());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(@RequestHeader("Authorization") String token) {
        String response = authService.logout(token);
        return ResponseEntity.ok(new ApiResponse<>(response));
    }
}
