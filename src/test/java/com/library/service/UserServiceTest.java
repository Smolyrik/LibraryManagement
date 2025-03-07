package com.library.service;

import com.library.dto.UserDto;
import com.library.entity.Role;
import com.library.entity.User;
import com.library.mapper.UserMapper;
import com.library.repository.UserRepository;
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
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private UserDto userDto;
    private User user;

    @BeforeEach
    public void setUp() {
        userDto = UserDto.builder()
                .userId(1)
                .username("johndoe")
                .email("john.doe@example.com")
                .password("password123")
                .role("ROLE_USER")
                .build();

        user = User.builder()
                .userId(1)
                .username("johndoe")
                .email("john.doe@example.com")
                .password("password123")
                .role(Role.ROLE_USER)
                .build();
    }

    @Test
    public void addUser_ShouldReturnUserDto() {
        when(userMapper.toEntity(any(UserDto.class))).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toDto(any(User.class))).thenReturn(userDto);

        UserDto result = userService.addUser(userDto);

        assertNotNull(result);
        assertEquals(userDto.getUsername(), result.getUsername());
        assertEquals(userDto.getEmail(), result.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void getUserById_ShouldReturnUserDto_WhenUserExists() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        when(userMapper.toDto(any(User.class))).thenReturn(userDto);

        UserDto result = userService.getUserById(1);

        assertNotNull(result);
        assertEquals(userDto.getUserId(), result.getUserId());
        verify(userRepository, times(1)).findById(1);
    }

    @Test
    public void getUserById_ShouldThrowException_WhenUserDoesNotExist() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> userService.getUserById(1));
        verify(userRepository, times(1)).findById(1);
    }

    @Test
    public void updateUser_ShouldReturnUpdatedUserDto() {
        when(userRepository.existsById(anyInt())).thenReturn(true);
        when(userMapper.toEntity(any(UserDto.class))).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toDto(any(User.class))).thenReturn(userDto);

        UserDto result = userService.updateUser(1, userDto);

        assertNotNull(result);
        assertEquals(userDto.getUsername(), result.getUsername());
        assertEquals(userDto.getEmail(), result.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void updateUser_ShouldThrowException_WhenUserDoesNotExist() {
        when(userRepository.existsById(anyInt())).thenReturn(false);

        assertThrows(NoSuchElementException.class, () -> userService.updateUser(1, userDto));
        verify(userRepository, times(1)).existsById(1);
    }

    @Test
    public void deleteUser_ShouldDeleteUser_WhenUserExists() {
        when(userRepository.existsById(anyInt())).thenReturn(true);

        userService.deleteUser(1);

        verify(userRepository, times(1)).deleteById(1);
    }

    @Test
    public void deleteUser_ShouldThrowException_WhenUserDoesNotExist() {
        when(userRepository.existsById(anyInt())).thenReturn(false);

        assertThrows(NoSuchElementException.class, () -> userService.deleteUser(1));
        verify(userRepository, times(1)).existsById(1);
    }
}
