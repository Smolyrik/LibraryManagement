package com.library.controller;

import com.library.dto.UserProfileDto;
import com.library.service.impl.UserProfileServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserProfileControllerTest {

    @Mock
    private UserProfileServiceImpl userProfileService;

    @InjectMocks
    private UserProfileController userProfileController;

    private UserProfileDto userProfileDto;
    private Integer userProfileId;

    @BeforeEach
    void setUp() {
        userProfileId = 1;
        userProfileDto = UserProfileDto.builder()
                .userProfileId(userProfileId)
                .firstName("John")
                .lastName("Doe")
                .phone("+1234567890")
                .address("123 Street, City")
                .userId(10)
                .build();
    }

    @Test
    void addUserProfile_ShouldReturnUserProfile() {
        when(userProfileService.addUserProfile(any(UserProfileDto.class))).thenReturn(userProfileDto);
        ResponseEntity<UserProfileDto> response = userProfileController.addUserProfile(userProfileDto);

        assertNotNull(response.getBody());
        assertEquals(userProfileDto.getUserProfileId(), response.getBody().getUserProfileId());
        verify(userProfileService, times(1)).addUserProfile(any(UserProfileDto.class));
    }

    @Test
    void getUserProfileById_ShouldReturnUserProfile() {
        when(userProfileService.getUserProfileById(userProfileId)).thenReturn(userProfileDto);
        ResponseEntity<UserProfileDto> response = userProfileController.getUserProfileById(userProfileId);

        assertNotNull(response.getBody());
        assertEquals(userProfileId, response.getBody().getUserProfileId());
        verify(userProfileService, times(1)).getUserProfileById(userProfileId);
    }

    @Test
    void getAllUserProfiles_ShouldReturnUserProfileList() {
        when(userProfileService.getAllUserProfiles()).thenReturn(Collections.singletonList(userProfileDto));
        ResponseEntity<List<UserProfileDto>> response = userProfileController.getAllUserProfiles();

        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
        verify(userProfileService, times(1)).getAllUserProfiles();
    }

    @Test
    void updateUserProfile_ShouldReturnUpdatedUserProfile() {
        when(userProfileService.updateUserProfile(eq(userProfileId), any(UserProfileDto.class))).thenReturn(userProfileDto);
        ResponseEntity<UserProfileDto> response = userProfileController.updateUserProfile(userProfileId, userProfileDto);

        assertNotNull(response.getBody());
        assertEquals(userProfileId, response.getBody().getUserProfileId());
        verify(userProfileService, times(1)).updateUserProfile(eq(userProfileId), any(UserProfileDto.class));
    }

    @Test
    void deleteUserProfile_ShouldReturnNoContent() {
        doNothing().when(userProfileService).deleteUserProfile(userProfileId);
        ResponseEntity<Void> response = userProfileController.deleteUserProfile(userProfileId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(userProfileService, times(1)).deleteUserProfile(userProfileId);
    }
}
