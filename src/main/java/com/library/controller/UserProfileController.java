package com.library.controller;

import com.library.dto.UserProfileDto;
import com.library.service.impl.UserProfileServiceImpl;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user-profiles")
@RequiredArgsConstructor
@Tag(name = "User Profile Management", description = "Operations related to user profiles")
public class UserProfileController {

    private final UserProfileServiceImpl userProfileService;

    @Operation(
            summary = "Add a new user profile",
            description = "Creates a new user profile in the system.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully created user profile",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UserProfileDto.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input")
            }
    )
    @PostMapping
    public ResponseEntity<UserProfileDto> addUserProfile(@Valid @RequestBody UserProfileDto userProfileDto) {
        return ResponseEntity.ok(userProfileService.addUserProfile(userProfileDto));
    }

    @Operation(
            summary = "Get user profile by ID",
            description = "Retrieves a user profile by their ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved user profile",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UserProfileDto.class))),
                    @ApiResponse(responseCode = "404", description = "User profile not found")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<UserProfileDto> getUserProfileById(@PathVariable int id) {
        return ResponseEntity.ok(userProfileService.getUserProfileById(id));
    }

    @Operation(
            summary = "Get all user profiles",
            description = "Retrieves a list of all user profiles.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of user profiles",
                            content = @Content(mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = UserProfileDto.class))))
            }
    )
    @GetMapping
    @Secured("ROLE_ADMIN")
    public ResponseEntity<List<UserProfileDto>> getAllUserProfiles() {
        return ResponseEntity.ok(userProfileService.getAllUserProfiles());
    }

    @Operation(
            summary = "Update user profile",
            description = "Updates an existing user profile's information.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully updated user profile",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UserProfileDto.class))),
                    @ApiResponse(responseCode = "404", description = "User profile not found")
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<UserProfileDto> updateUserProfile(@PathVariable int id, @Valid @RequestBody UserProfileDto userProfileDto) {
        return ResponseEntity.ok(userProfileService.updateUserProfile(id, userProfileDto));
    }

    @Operation(
            summary = "Delete user profile",
            description = "Deletes a user profile by their ID.",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "204", description = "Successfully deleted user profile"),
                    @ApiResponse(responseCode = "404", description = "User profile not found")
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserProfile(@PathVariable int id) {
        userProfileService.deleteUserProfile(id);
        return ResponseEntity.noContent().build();
    }
}
