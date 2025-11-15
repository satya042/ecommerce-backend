package com.ecommerce.userservice.controller;

import com.ecommerce.userservice.model.request.ChangePasswordRequest;
import com.ecommerce.userservice.model.request.UpdateUserRequest;
import com.ecommerce.userservice.model.response.ApiResponse;
import com.ecommerce.userservice.model.response.UserResponse;
import com.ecommerce.userservice.service.UserService;
import com.ecommerce.userservice.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(@RequestHeader("Authorization") String token) {
        try {
            String username = jwtUtil.extractUsername(token.substring(7)); // Remove "Bearer " prefix
            UserResponse userResponse = userService.getCurrentUser(username);
            return ResponseEntity.ok(ApiResponse.success("User profile retrieved successfully", userResponse));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserResponse>> updateCurrentUser(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody UpdateUserRequest updateRequest) {
        try {
            String username = jwtUtil.extractUsername(token.substring(7));
            UserResponse userResponse = userService.updateUser(username, updateRequest);
            return ResponseEntity.ok(ApiResponse.success("User profile updated successfully", userResponse));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/change-password")
    public ResponseEntity<ApiResponse<String>> changePassword(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
        try {
            String username = jwtUtil.extractUsername(token.substring(7));
            String response = userService.changePassword(username, changePasswordRequest);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/profile")
    public ResponseEntity<ApiResponse<String>> deleteCurrentUser(@RequestHeader("Authorization") String token) {
        try {
            String username = jwtUtil.extractUsername(token.substring(7));
            String response = userService.deleteUser(username);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // Admin endpoints
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        try {
            List<UserResponse> users = userService.getAllUsers();
            return ResponseEntity.ok(ApiResponse.success("Users retrieved successfully", users));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long userId) {
        try {
            UserResponse userResponse = userService.getUserById(userId);
            return ResponseEntity.ok(ApiResponse.success("User retrieved successfully", userResponse));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> updateUserById(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateUserRequest updateRequest) {
        try {
            UserResponse userResponse = userService.updateUserById(userId, updateRequest);
            return ResponseEntity.ok(ApiResponse.success("User updated successfully", userResponse));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteUserById(@PathVariable Long userId) {
        try {
            String response = userService.deleteUserById(userId);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{userId}/enable")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> enableUser(@PathVariable Long userId) {
        try {
            String response = userService.enableUser(userId);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{userId}/disable")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> disableUser(@PathVariable Long userId) {
        try {
            String response = userService.disableUser(userId);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}
