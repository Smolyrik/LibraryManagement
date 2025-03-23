package com.library.IT.controller;

import com.library.dto.UserProfileDto;
import com.library.entity.Role;
import com.library.entity.User;
import com.library.entity.UserProfile;
import com.library.repository.UserProfileRepository;
import com.library.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClient;

import java.security.Key;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class UserProfileControllerIT {

    @LocalServerPort
    private int port;

    private WebClient webClient;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private UserRepository userRepository;

    @Value("${token.signing.key}")
    private String jwtSigningKey;

    private User testUser;

    @BeforeEach
    void setUp() {
        String baseUrl = "http://localhost:" + port + "/api/user-profiles";
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();

        testUser = userRepository.save(new User(null, "Test User", "test@example.com", "hashedpassword", Role.ROLE_ADMIN));
    }

    @AfterEach
    void cleanUp() {
        userProfileRepository.deleteAll();
        userProfileRepository.flush();
        userRepository.deleteAll();
        userRepository.flush();
    }

    @Test
    void testAddUserProfile() {
        UserProfileDto userProfileDto = new UserProfileDto(null, "John", "Doe", "+1234567890", "123 Main St", testUser.getUserId());

        UserProfileDto response = webClient.post()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateTestToken())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userProfileDto)
                .retrieve()
                .bodyToMono(UserProfileDto.class)
                .block();

        assertThat(response).isNotNull();
        assertThat(response.getFirstName()).isEqualTo("John");
        assertThat(response.getUserId()).isEqualTo(testUser.getUserId());
    }

    @Test
    void testGetUserProfileById() {
        UserProfile userProfile = userProfileRepository.save(new UserProfile(null, "Jane", "Doe", "+9876543210", "456 Elm St", testUser));

        UserProfileDto response = webClient.get()
                .uri("/" + userProfile.getUserProfileId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateTestToken())
                .retrieve()
                .bodyToMono(UserProfileDto.class)
                .block();

        assertThat(response).isNotNull();
        assertThat(response.getFirstName()).isEqualTo("Jane");
    }

    @Test
    void testGetAllUserProfiles() {
        User testUser1 = userRepository.save(new User(null, "Test User1", "test1@example.com", "hashedpassword", Role.ROLE_ADMIN));

        userProfileRepository.save(new UserProfile(null, "Alice", "Smith", "+1111111111", "789 Oak St", testUser));
        userProfileRepository.save(new UserProfile(null, "Bob", "Brown", "+2222222222", "101 Pine St", testUser1));

        UserProfileDto[] response = webClient.get()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateTestToken())
                .retrieve()
                .bodyToMono(UserProfileDto[].class)
                .block();

        assertThat(response).isNotNull();
        assertThat(response.length).isGreaterThanOrEqualTo(2);
    }

    @Test
    void testUpdateUserProfile() {
        UserProfile userProfile = userProfileRepository.save(new UserProfile(null, "Charlie", "Williams", "+3333333333", "202 Maple St", testUser));
        UserProfileDto updatedProfile = new UserProfileDto(userProfile.getUserProfileId(), "Charlie", "Johnson", "+3333333333", "202 Maple St", testUser.getUserId());

        UserProfileDto response = webClient.put()
                .uri("/" + userProfile.getUserProfileId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateTestToken())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updatedProfile)
                .retrieve()
                .bodyToMono(UserProfileDto.class)
                .block();

        assertThat(response).isNotNull();
        assertThat(response.getLastName()).isEqualTo("Johnson");
    }

    @Test
    void testDeleteUserProfile() {
        UserProfile userProfile = userProfileRepository.save(new UserProfile(null, "David", "Evans", "+4444444444", "303 Birch St", testUser));

        webClient.delete()
                .uri("/" + userProfile.getUserProfileId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateTestToken())
                .retrieve()
                .bodyToMono(Void.class)
                .block();

        assertThat(userProfileRepository.findById(userProfile.getUserProfileId())).isEmpty();
    }

    private String generateTestToken() {
        Key key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSigningKey));

        return Jwts.builder()
                .subject("Test User")
                .claim("authorities", List.of("ROLE_ADMIN"))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(key)
                .compact();
    }
}
