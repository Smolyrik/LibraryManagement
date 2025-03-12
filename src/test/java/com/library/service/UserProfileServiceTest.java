package com.library.service;

import com.library.dto.UserProfileDto;
import com.library.entity.UserProfile;
import com.library.mapper.UserProfileMapper;
import com.library.repository.UserProfileRepository;
import com.library.service.impl.UserProfileServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class UserProfileServiceTest {

    @Mock
    private UserProfileRepository userProfileRepository;

    @Mock
    private UserProfileMapper userProfileMapper;

    @InjectMocks
    private UserProfileServiceImpl userProfileService;

    private UserProfileDto userProfileDto;
    private UserProfile userProfile;

    @BeforeEach
    public void setUp() {
        userProfileDto = UserProfileDto.builder()
                .userProfileId(1)
                .firstName("John")
                .lastName("Doe")
                .phone("+1234567890")
                .address("123 Main St")
                .userId(1)
                .build();

        userProfile = UserProfile.builder()
                .userProfileId(1)
                .firstName("John")
                .lastName("Doe")
                .phone("+1234567890")
                .address("123 Main St")
                .user(null)
                .build();
    }

    @Test
    public void addUserProfile_ShouldReturnUserProfileDto() {
        when(userProfileMapper.toEntity(any(UserProfileDto.class))).thenReturn(userProfile);
        when(userProfileRepository.save(any(UserProfile.class))).thenReturn(userProfile);
        when(userProfileMapper.toDto(any(UserProfile.class))).thenReturn(userProfileDto);

        UserProfileDto result = userProfileService.addUserProfile(userProfileDto);

        assertNotNull(result);
        assertEquals(userProfileDto.getFirstName(), result.getFirstName());
        assertEquals(userProfileDto.getLastName(), result.getLastName());
        verify(userProfileRepository, times(1)).save(any(UserProfile.class));
    }

    @Test
    public void getUserProfileById_ShouldReturnUserProfileDto_WhenUserProfileExists() {
        when(userProfileRepository.findById(anyInt())).thenReturn(Optional.of(userProfile));
        when(userProfileMapper.toDto(any(UserProfile.class))).thenReturn(userProfileDto);

        UserProfileDto result = userProfileService.getUserProfileById(1);

        assertNotNull(result);
        assertEquals(userProfileDto.getUserProfileId(), result.getUserProfileId());
        verify(userProfileRepository, times(1)).findById(1);
    }

    @Test
    public void getUserProfileById_ShouldThrowException_WhenUserProfileDoesNotExist() {
        when(userProfileRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> userProfileService.getUserProfileById(1));
        verify(userProfileRepository, times(1)).findById(1);
    }

    @Test
    public void updateUserProfile_ShouldReturnUpdatedUserProfileDto() {
        when(userProfileRepository.existsById(anyInt())).thenReturn(true);
        when(userProfileMapper.toEntity(any(UserProfileDto.class))).thenReturn(userProfile);
        when(userProfileRepository.save(any(UserProfile.class))).thenReturn(userProfile);
        when(userProfileMapper.toDto(any(UserProfile.class))).thenReturn(userProfileDto);

        UserProfileDto result = userProfileService.updateUserProfile(1, userProfileDto);

        assertNotNull(result);
        assertEquals(userProfileDto.getFirstName(), result.getFirstName());
        assertEquals(userProfileDto.getLastName(), result.getLastName());
        verify(userProfileRepository, times(1)).save(any(UserProfile.class));
    }

    @Test
    public void updateUserProfile_ShouldThrowException_WhenUserProfileDoesNotExist() {
        when(userProfileRepository.existsById(anyInt())).thenReturn(false);

        assertThrows(NoSuchElementException.class, () -> userProfileService.updateUserProfile(1, userProfileDto));
        verify(userProfileRepository, times(1)).existsById(1);
    }

    @Test
    public void deleteUserProfile_ShouldDeleteUserProfile_WhenUserProfileExists() {
        when(userProfileRepository.existsById(anyInt())).thenReturn(true);

        userProfileService.deleteUserProfile(1);

        verify(userProfileRepository, times(1)).deleteById(1);
    }

    @Test
    public void deleteUserProfile_ShouldThrowException_WhenUserProfileDoesNotExist() {
        when(userProfileRepository.existsById(anyInt())).thenReturn(false);

        assertThrows(NoSuchElementException.class, () -> userProfileService.deleteUserProfile(1));
        verify(userProfileRepository, times(1)).existsById(1);
    }
}

