package com.library.IT.service;

import com.library.dto.UserDto;
import com.library.dto.UserProfileDto;
import com.library.entity.Role;
import com.library.entity.User;
import com.library.entity.UserProfile;
import com.library.mapper.UserMapper;
import com.library.mapper.UserProfileMapper;
import com.library.repository.UserProfileRepository;
import com.library.repository.UserRepository;
import com.library.service.impl.UserProfileServiceImpl;
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
public class UserProfileServiceIT {

    @Autowired
    private UserProfileServiceImpl userProfileService;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserProfileMapper userProfileMapper;

    @Autowired
    private UserServiceImpl userService;

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
        userProfileRepository.deleteAll();
        userProfileRepository.flush();
        userRepository.deleteAll();
        userRepository.flush();
    }

    private UserDto createTestUser(String test) {
        User testUser = User.builder()
                .email(test +"@test.com")
                .username(test)
                .password(test)
                .role(Role.ROLE_USER)
                .build();
        return userService.addUser(userMapper.toDto(testUser));
    }

    @Test
    void addUserProfile_ShouldSaveAndReturnUserProfileDto() {
        UserDto testUser = createTestUser("test");
        UserProfile testUserProfile = UserProfile.builder()
                .user(userMapper.toEntity(testUser))
                .firstName("Test")
                .lastName("User")
                .phone("+123456789")
                .address("TestAddress")
                .build();
        UserProfileDto savedUserProfile = userProfileService.addUserProfile(userProfileMapper.toDto(testUserProfile));

        assertNotNull(savedUserProfile);
        assertNotNull(savedUserProfile.getUserProfileId());
        assertEquals(testUserProfile.getFirstName(), savedUserProfile.getFirstName());
        assertEquals(testUserProfile.getLastName(), savedUserProfile.getLastName());
        assertEquals(testUserProfile.getPhone(), savedUserProfile.getPhone());
        assertEquals(testUserProfile.getAddress(), savedUserProfile.getAddress());
    }

    @Test
    void getUserProfileById_ShouldReturnUserProfileDto() {
        UserDto testUser = createTestUser("test");
        UserProfile testUserProfile = UserProfile.builder()
                .user(userMapper.toEntity(testUser))
                .firstName("Test")
                .lastName("User")
                .phone("+123456789")
                .address("TestAddress")
                .build();
        UserProfileDto savedUserProfile = userProfileService.addUserProfile(userProfileMapper.toDto(testUserProfile));
        UserProfileDto foundUserProfile = userProfileService.getUserProfileById(savedUserProfile.getUserProfileId());

        assertNotNull(foundUserProfile);
        assertEquals(savedUserProfile.getUserProfileId(), foundUserProfile.getUserProfileId());
    }

    @Test
    void getAllUserProfiles_ShouldReturnListOfUserProfileDto() {
        UserDto testUser1 = createTestUser("test1");
        UserDto testUser2 = createTestUser("test2");
        UserProfile testUserProfile1 = UserProfile.builder()
                .user(userMapper.toEntity(testUser1))
                .firstName("John")
                .lastName("Doe")
                .phone("+111111111")
                .address("Address1")
                .build();
        userProfileService.addUserProfile(userProfileMapper.toDto(testUserProfile1));

        UserProfile testUserProfile2 = UserProfile.builder()
                .user(userMapper.toEntity(testUser2))
                .firstName("Jane")
                .lastName("Doe")
                .phone("+222222222")
                .address("Address2")
                .build();
        userProfileService.addUserProfile(userProfileMapper.toDto(testUserProfile2));

        List<UserProfileDto> foundProfiles = userProfileService.getAllUserProfiles();

        assertEquals(2, foundProfiles.size());
    }

    @Test
    void updateUserProfile_ShouldUpdateAndReturnUserProfileDto() {
        UserDto testUser = createTestUser("test");
        UserProfile testUserProfile = UserProfile.builder()
                .user(userMapper.toEntity(testUser))
                .firstName("Test")
                .lastName("User")
                .phone("+123456789")
                .address("TestAddress")
                .build();
        UserProfileDto savedUserProfile = userProfileService.addUserProfile(userProfileMapper.toDto(testUserProfile));
        savedUserProfile.setPhone("+987654321");

        UserProfileDto updatedUserProfile = userProfileService.updateUserProfile(savedUserProfile.getUserProfileId(), savedUserProfile);

        assertNotNull(updatedUserProfile);
        assertEquals(updatedUserProfile.getPhone(), savedUserProfile.getPhone());
    }

    @Test
    void deleteUserProfile_ShouldDeleteUserProfile() {
        UserDto testUser = createTestUser("test");
        UserProfile testUserProfile = UserProfile.builder()
                .user(userMapper.toEntity(testUser))
                .firstName("Test")
                .lastName("User")
                .phone("+123456789")
                .address("TestAddress")
                .build();
        UserProfileDto savedUserProfile = userProfileService.addUserProfile(userProfileMapper.toDto(testUserProfile));
        userProfileService.deleteUserProfile(savedUserProfile.getUserProfileId());

        assertThrows(NoSuchElementException.class, () -> userProfileService.getUserProfileById(savedUserProfile.getUserProfileId()));
    }
}
