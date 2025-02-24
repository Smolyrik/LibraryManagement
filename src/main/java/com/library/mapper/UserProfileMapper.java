package com.library.mapper;

import com.library.dto.UserProfileDto;
import com.library.entity.UserProfile;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserProfileMapper {

    @Mapping(source = "user.userId", target = "userId")
    UserProfileDto toDto(UserProfile userProfile);

    @Mapping(source = "userId", target = "user.userId")
    UserProfile toEntity(UserProfileDto userProfileDto);

    List<UserProfileDto> toDtos(List<UserProfile> userProfiles);
    List<UserProfile> toEntities(List<UserProfileDto> userProfileDtos);

}
