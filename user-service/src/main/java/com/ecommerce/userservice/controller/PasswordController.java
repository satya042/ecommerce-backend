package com.ecommerce.userservice.controller;

import com.ecommerce.userservice.advice.ApiResponse;
import com.ecommerce.userservice.payload.request.ForgotPasswordRequest;
import com.ecommerce.userservice.payload.request.ResetPasswordRequest;
import com.ecommerce.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/password")
@RequiredArgsConstructor
@Slf4j
public class PasswordController {

    private final UserService userService;

    @PostMapping("/forgot")
    public ResponseEntity<ApiResponse<String>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        log.info("Forgot password endpoint: Email={}", forgotPasswordRequest.getEmail());
        String response = userService.forgotPassword(forgotPasswordRequest);
        log.info("Forgot password endpoint response: Email={}, Status=200", forgotPasswordRequest.getEmail());
        return ResponseEntity.ok(new ApiResponse<>(response));
    }

    @PostMapping("/reset")
    public ResponseEntity<ApiResponse<String>> resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
        log.info("Reset password endpoint");
        String response = userService.resetPassword(resetPasswordRequest);
        log.info("Reset password endpoint response: Status=200");
        return ResponseEntity.ok(new ApiResponse<>(response));
    }
}
