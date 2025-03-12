package com.library.IT;

import com.library.dto.UserDto;
import com.library.entity.Role;
import com.library.entity.User;
import com.library.mapper.UserMapper;
import com.library.repository.UserRepository;
import com.library.service.impl.UserServiceImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ExtendWith(SpringExtension.class)
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserServiceIT {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16.0")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @BeforeAll
    static void setup() {
        System.setProperty("spring.datasource.url", postgres.getJdbcUrl());
        System.setProperty("spring.datasource.username", postgres.getUsername());
        System.setProperty("spring.datasource.password", postgres.getPassword());
    }

    @AfterAll
    static void teardown() {
        postgres.stop();
    }

    @AfterEach
    public void cleanUp() {
        userRepository.deleteAll();
        userRepository.flush();
    }

    @Test
    void addUser_ShouldSaveAndReturnUserDto() {
        User testUser = User.builder()
                .email("test@test.com")
                .username("test")
                .password("test")
                .role(Role.ROLE_USER)
                .build();

        UserDto savedUser = userService.addUser(userMapper.toDto(testUser));

        assertNotNull(savedUser);
        assertNotNull(savedUser.getUserId());
        assertEquals(savedUser.getEmail(), testUser.getEmail());
        assertEquals(savedUser.getUsername(), testUser.getUsername());
        assertEquals(savedUser.getPassword(), testUser.getPassword());
        assertEquals(savedUser.getRole(), testUser.getRole().toString());
    }

    @Test
    void getUserById_ShouldReturnUserDto() {
        User testUser = User.builder()
                .email("test@test.com")
                .username("test")
                .password("test")
                .role(Role.ROLE_USER)
                .build();

        UserDto savedUser = userService.addUser(userMapper.toDto(testUser));
        UserDto foundUser = userService.getUserById(savedUser.getUserId());

        assertNotNull(foundUser);
        assertEquals(foundUser.getUserId(), savedUser.getUserId());
        assertEquals(foundUser.getEmail(), savedUser.getEmail());
        assertEquals(foundUser.getUsername(), savedUser.getUsername());
        assertEquals(foundUser.getPassword(), savedUser.getPassword());
        assertEquals(foundUser.getRole(), savedUser.getRole());
    }

    @Test
    void getAllUsers_ShouldReturnListOfUserDto() {
        User testUser1 = User.builder()
                .email("test1@test.com")
                .username("test1")
                .password("test1")
                .role(Role.ROLE_USER)
                .build();
        userService.addUser(userMapper.toDto(testUser1));

        User testUser2 = User.builder()
                .email("test2@test.com")
                .username("test2")
                .password("test2")
                .role(Role.ROLE_USER)
                .build();
        userService.addUser(userMapper.toDto(testUser2));

        List<UserDto> foundUsers = userService.getAllUsers();

        assertEquals(2, foundUsers.size());
    }

    @Test
    void updateUser_ShouldUpdateAndReturnUserDto() {
        User testUser = User.builder()
                .email("test@test.com")
                .username("test")
                .password("test")
                .role(Role.ROLE_USER)
                .build();
        UserDto savedUser = userService.addUser(userMapper.toDto(testUser));
        savedUser.setPassword("newPassword");

        UserDto updatedUser = userService.updateUser(savedUser.getUserId(), savedUser);

        assertNotNull(updatedUser);
        assertEquals(updatedUser.getPassword(), savedUser.getPassword());
    }

    @Test
    void deleteUser_ShouldDeleteUser() {
        User testUser = User.builder()
                .email("test@test.com")
                .username("test")
                .password("test")
                .role(Role.ROLE_USER)
                .build();
        UserDto savedUser = userService.addUser(userMapper.toDto(testUser));
        userService.deleteUser(savedUser.getUserId());

        assertThrows(NoSuchElementException.class, () -> userService.getUserById(savedUser.getUserId()));

    }
}
