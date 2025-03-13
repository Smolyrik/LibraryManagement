package com.library.controller;

import com.library.dto.UserDto;
import com.library.service.impl.UserServiceImpl;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
@Tag(name = "User Management", description = "Operations related to users")
public class UserController {

    private final UserServiceImpl userService;

    @Operation(
            summary = "Add a new user",
            description = "Creates a new user in the system.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Successfully created user",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UserDto.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input")
            }
    )
    @PostMapping
    public ResponseEntity<UserDto> addUser(@Valid @RequestBody UserDto userDto) {
        UserDto savedUser = userService.addUser(userDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    @Operation(
            summary = "Get user by ID",
            description = "Retrieves a user by their ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved user",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UserDto.class))),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Integer userId) {
        UserDto user = userService.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    @Operation(
            summary = "Get all users",
            description = "Retrieves a list of all users.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of users",
                            content = @Content(mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = UserDto.class))))
            }
    )
    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @Operation(
            summary = "Update user",
            description = "Updates an existing user's information.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully updated user",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UserDto.class))),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    @PutMapping("/{userId}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Integer userId,
                                              @Valid @RequestBody UserDto userDto) {
        UserDto updatedUser = userService.updateUser(userId, userDto);
        return ResponseEntity.ok(updatedUser);
    }

    @Operation(
            summary = "Delete user",
            description = "Deletes a user by their ID.",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "204", description = "Successfully deleted user"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
