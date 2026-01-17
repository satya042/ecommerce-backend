package com.ecommerce.userservice.controller;

import com.ecommerce.userservice.advice.ApiResponse;
import com.ecommerce.userservice.payload.request.ChangePasswordRequest;
import com.ecommerce.userservice.payload.request.UpdateUserRequest;
import com.ecommerce.userservice.payload.response.UserResponse;
import com.ecommerce.userservice.service.UserService;
import com.ecommerce.userservice.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<UserResponse> getCurrentUser(@RequestHeader("Authorization") String token) {
        log.info("Get profile endpoint");
        UserResponse userResponse = userService.getCurrentUser(token);
        log.info("Get profile endpoint response: Username={}, Status=200", userResponse.getUsername());
        return ResponseEntity.ok(userResponse);
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserResponse>> updateCurrentUser(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody UpdateUserRequest updateRequest) {
        log.info("Update profile endpoint");
        UserResponse userResponse = userService.updateUser(token, updateRequest);
        log.info("Update profile endpoint response: Username={}, Status=200", userResponse.getUsername());
        return ResponseEntity.ok(new ApiResponse<>(userResponse, "User profile updated successfully"));
    }

    @PutMapping("/change-password")
    public ResponseEntity<ApiResponse<String>> changePassword(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody ChangePasswordRequest changePasswordRequest) {

            String response = userService.changePassword(token, changePasswordRequest);
            return ResponseEntity.ok(new ApiResponse<>(response));
    }

    // Admin endpoints
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
}
