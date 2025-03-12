package com.library.service;

import com.library.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto addUser(UserDto userDto);

    UserDto getUserById(Integer userId);

    List<UserDto> getAllUsers();

    UserDto updateUser(Integer userId, UserDto userDto);

    void deleteUser(Integer userId);
}
