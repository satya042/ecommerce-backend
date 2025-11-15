package com.ecommerce.userservice.controller;

import com.ecommerce.userservice.model.request.ForgotPasswordRequest;
import com.ecommerce.userservice.model.request.ResetPasswordRequest;
import com.ecommerce.userservice.model.response.ApiResponse;
import com.ecommerce.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/password")
@RequiredArgsConstructor
public class PasswordController {

    private final UserService userService;

    @PostMapping("/forgot")
    public ResponseEntity<ApiResponse<String>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        try {
            String response = userService.forgotPassword(forgotPasswordRequest);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/reset")
    public ResponseEntity<ApiResponse<String>> resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
        try {
            String response = userService.resetPassword(resetPasswordRequest);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}
