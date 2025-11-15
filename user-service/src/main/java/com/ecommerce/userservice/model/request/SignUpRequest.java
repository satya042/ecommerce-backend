package com.ecommerce.userservice.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequest {
    @NotBlank(message = "Full name must not be blank")
    @Size(min = 2, max = 50, message = "Full name must be between 2 and 50 characters")
    private String fullname;

    @NotBlank(message = "Username must not be blank")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username can only contain letters, numbers, and underscores")
    private String username;

    @NotBlank(message = "Email must not be blank")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password must not be blank")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$", 
             message = "Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character")
    private String password;

    @NotBlank(message = "Gender must not be blank")
    @Pattern(regexp = "^(MALE|FEMALE|OTHER)$", message = "Gender must be MALE, FEMALE, or OTHER")
    private String gender;

    @Pattern(regexp = "^(https?://.*)?$", message = "Avatar must be a valid HTTP or HTTPS URL")
    private String avatar;

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    private String phone;
}
