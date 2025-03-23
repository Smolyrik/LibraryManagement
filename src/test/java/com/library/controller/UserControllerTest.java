package com.library.controller;

import com.library.dto.UserDto;
import com.library.service.impl.UserServiceImpl;
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
class UserControllerTest {

    @Mock
    private UserServiceImpl userService;

    @InjectMocks
    private UserController userController;

    private UserDto userDto;
    private Integer userId;

    @BeforeEach
    void setUp() {
        userId = 1;
        userDto = UserDto.builder()
                .userId(userId)
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .role("ROLE_USER")
                .build();
    }

    @Test
    void addUser_ShouldReturnCreatedUser() {
        when(userService.addUser(any(UserDto.class))).thenReturn(userDto);
        ResponseEntity<UserDto> response = userController.addUser(userDto);

        assertNotNull(response.getBody());
        assertEquals(userDto.getUserId(), response.getBody().getUserId());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(userService, times(1)).addUser(any(UserDto.class));
    }

    @Test
    void getUserById_ShouldReturnUser() {
        when(userService.getUserById(userId)).thenReturn(userDto);
        ResponseEntity<UserDto> response = userController.getUserById(userId);

        assertNotNull(response.getBody());
        assertEquals(userId, response.getBody().getUserId());
        verify(userService, times(1)).getUserById(userId);
    }

    @Test
    void getAllUsers_ShouldReturnUserList() {
        when(userService.getAllUsers()).thenReturn(Collections.singletonList(userDto));
        ResponseEntity<List<UserDto>> response = userController.getAllUsers();

        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void updateUser_ShouldReturnUpdatedUser() {
        when(userService.updateUser(eq(userId), any(UserDto.class))).thenReturn(userDto);
        ResponseEntity<UserDto> response = userController.updateUser(userId, userDto);

        assertNotNull(response.getBody());
        assertEquals(userId, response.getBody().getUserId());
        verify(userService, times(1)).updateUser(eq(userId), any(UserDto.class));
    }

    @Test
    void deleteUser_ShouldReturnNoContent() {
        doNothing().when(userService).deleteUser(userId);
        ResponseEntity<Void> response = userController.deleteUser(userId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(userService, times(1)).deleteUser(userId);
    }
}
