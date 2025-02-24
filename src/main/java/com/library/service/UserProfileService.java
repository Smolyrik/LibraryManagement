package com.library.service;

import com.library.dto.UserProfileDto;
import com.library.entity.UserProfile;
import com.library.mapper.UserProfileMapper;
import com.library.repository.UserProfileRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final UserProfileMapper userProfileMapper;

    @Transactional
    public UserProfileDto addUserProfile(UserProfileDto userProfileDto) {
        UserProfile userProfile = userProfileMapper.toEntity(userProfileDto);
        UserProfile savedUserProfile = userProfileRepository.save(userProfile);
        log.info("Added new user profile with ID: {}", savedUserProfile.getUserProfileId());
        return userProfileMapper.toDto(savedUserProfile);
    }

    public UserProfileDto getUserProfileById(int userProfileId) {
        return userProfileRepository.findById(userProfileId)
                .map(userProfileMapper::toDto)
                .orElseThrow(() -> {
                    log.error("User profile with ID: {} not found", userProfileId);
                    return new NoSuchElementException("User profile with ID: " + userProfileId + " not found");
                });
    }

    public List<UserProfileDto> getAllUserProfiles() {
        log.info("Fetching all user profiles");
        return userProfileRepository.findAll().stream()
                .map(userProfileMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserProfileDto updateUserProfile(int userProfileId, UserProfileDto userProfileDto) {
        if (!userProfileRepository.existsById(userProfileId)) {
            log.error("User profile with ID: {} not found", userProfileId);
            throw new NoSuchElementException("User profile with ID: " + userProfileId + " not found");
        }

        UserProfile updatedUserProfile = userProfileMapper.toEntity(userProfileDto);
        updatedUserProfile.setUserProfileId(userProfileId);

        UserProfile savedUserProfile = userProfileRepository.save(updatedUserProfile);
        log.info("Updated user profile with ID: {}", savedUserProfile.getUserProfileId());

        return userProfileMapper.toDto(savedUserProfile);
    }

    @Transactional
    public void deleteUserProfile(int userProfileId) {
        if (!userProfileRepository.existsById(userProfileId)) {
            log.error("User profile with ID: {} not found", userProfileId);
            throw new NoSuchElementException("User profile with ID: " + userProfileId + " not found");
        }
        userProfileRepository.deleteById(userProfileId);
        log.info("Deleted user profile with ID: {}", userProfileId);
    }
}
