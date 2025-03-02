package com.library.mapper;

import com.library.dto.UserDto;
import com.library.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "role", target = "role")
    UserDto toDto(User user);

    @Mapping(source = "role", target = "role")
    User toEntity(UserDto dto);


}
