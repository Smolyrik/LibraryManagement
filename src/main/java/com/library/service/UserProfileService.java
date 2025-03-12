package com.library.service;

import com.library.dto.UserProfileDto;

import java.util.List;

public interface UserProfileService {

    UserProfileDto addUserProfile(UserProfileDto userProfileDto);

    UserProfileDto getUserProfileById(int userProfileId);

    List<UserProfileDto> getAllUserProfiles();

    UserProfileDto updateUserProfile(int userProfileId, UserProfileDto userProfileDto);

    void deleteUserProfile(int userProfileId);
}
