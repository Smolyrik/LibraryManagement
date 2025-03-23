package com.library.IT.controller;

import com.library.dto.UserDto;
import com.library.entity.Role;
import com.library.entity.User;
import com.library.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
class UserControllerIT {

    @LocalServerPort
    private int port;

    private WebClient webClient;

    @Autowired
    private UserRepository userRepository;


    @Value("${token.signing.key}")
    private String jwtSigningKey;

    @BeforeEach
    void setUp() {
        String baseUrl = "http://localhost:" + port + "/api/users";
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();

        userRepository.save(new User(null, "Test User", "test@example.com", "hashedpassword", Role.ROLE_ADMIN));
    }

    @AfterEach
    void cleanUp() {
        userRepository.deleteAll();
        userRepository.flush();
    }

    @Test
    void testAddUser() {
        UserDto userDto = new UserDto(null, "Test User1", "test1@example.com", "hashedpassword", "ROLE_ADMIN");

        UserDto response = webClient.post()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateTestToken())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userDto)
                .retrieve()
                .bodyToMono(UserDto.class)
                .block();

        assertThat(response).isNotNull();
        assertThat(response.getEmail()).isEqualTo("test1@example.com");
    }

    @Test
    void testGetUserById() {
        User user = userRepository.save(new User(null, "Test User1", "test1@example.com", "hashedpassword", Role.ROLE_ADMIN));

        UserDto response = webClient.get()
                .uri("/" + user.getUserId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateTestToken())
                .retrieve()
                .bodyToMono(UserDto.class)
                .block();

        assertThat(response).isNotNull();
        assertThat(response.getEmail()).isEqualTo("test1@example.com");
    }

    @Test
    void testGetAllUsers() {
        userRepository.save(new User(null, "Test User1", "test1@example.com", "hashedpassword", Role.ROLE_ADMIN));
        userRepository.save(new User(null, "Test User2", "test2@example.com", "hashedpassword", Role.ROLE_ADMIN));

        UserDto[] response = webClient.get()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateTestToken())
                .retrieve()
                .bodyToMono(UserDto[].class)
                .block();

        assertThat(response).isNotNull();
        assertThat(response.length).isGreaterThanOrEqualTo(2);
    }

    @Test
    void testUpdateUser() {
        User user = userRepository.save(new User(null, "Test User1", "test1@example.com", "hashedpassword", Role.ROLE_ADMIN));
        UserDto updatedUser = new UserDto(user.getUserId(), "Updated User", "updated@example.com", "newpassword", "ROLE_ADMIN");

        UserDto response = webClient.put()
                .uri("/" + user.getUserId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateTestToken())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updatedUser)
                .retrieve()
                .bodyToMono(UserDto.class)
                .block();

        assertThat(response).isNotNull();
        assertThat(response.getEmail()).isEqualTo("updated@example.com");
    }

    @Test
    void testDeleteUser() {
        User user = userRepository.save(new User(null, "Test User1", "test1@example.com", "hashedpassword", Role.ROLE_ADMIN));

        webClient.delete()
                .uri("/" + user.getUserId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateTestToken())
                .retrieve()
                .bodyToMono(Void.class)
                .block();

        assertThat(userRepository.findById(user.getUserId())).isEmpty();
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
