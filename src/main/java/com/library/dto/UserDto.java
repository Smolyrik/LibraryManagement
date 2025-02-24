package com.library.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private Integer userId;

    @NotNull(message = "Username must not be null")
    @Size(min = 6, max = 50, message = "Username must be between 6 and 50 characters")
    private String username;

    @Email(message = "Email must be a valid email address")
    @NotNull(message = "Email must not be null")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    private String email;

    @NotBlank(message = "Password must not be null")
    @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
    private String password;

    @NotNull(message = "Role must not be null")
    @Pattern(regexp = "ROLE_ADMIN|ROLE_USER|ROLE_MODERATOR",
            message = "Role must be ROLE_ADMIN, ROLE_USER, or ROLE_MODERATOR")
    private String role;
}
