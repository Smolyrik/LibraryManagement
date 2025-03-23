package com.library.IT.controller;

import com.library.dto.AuthorDto;
import com.library.entity.Author;
import com.library.entity.Role;
import com.library.entity.User;
import com.library.repository.AuthorRepository;
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
class AuthorControllerIT {

    @LocalServerPort
    private int port;

    private WebClient webClient;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private UserRepository userRepository;

    @Value("${token.signing.key}")
    private String jwtSigningKey;

    @BeforeEach
    void setUp() {
        String baseUrl = "http://localhost:" + port + "/api/authors";
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();

        userRepository.save(new User(null, "Test User", "test@example.com", "hashedpassword", Role.ROLE_ADMIN));
    }

    @AfterEach
    void cleanUp() {
        userRepository.deleteAll();
        userRepository.flush();

        authorRepository.deleteAll();
        authorRepository.flush();
    }

    @Test
    void testAddAuthor() {
        AuthorDto authorDto = new AuthorDto(null, "Jane", "Doe", "Biography of Jane Doe");

        AuthorDto response = webClient.post()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateTestToken())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(authorDto)
                .retrieve()
                .bodyToMono(AuthorDto.class)
                .block();

        assertThat(response).isNotNull();
        assertThat(response.getFirstName()).isEqualTo("Jane");
    }

    @Test
    void testGetAuthorById() {
        Author author = authorRepository.save(new Author(null, "Jane", "Doe", "Biography of Jane Doe"));

        AuthorDto response = webClient.get()
                .uri("/" + author.getAuthorId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateTestToken())
                .retrieve()
                .bodyToMono(AuthorDto.class)
                .block();

        assertThat(response).isNotNull();
        assertThat(response.getFirstName()).isEqualTo("Jane");
    }

    @Test
    void testGetAllAuthors() {
        authorRepository.save(new Author(null, "Alice", "Smith", "Biography of Alice Smith"));
        authorRepository.save(new Author(null, "Bob", "Johnson", "Biography of Bob Johnson"));

        AuthorDto[] response = webClient.get()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateTestToken())
                .retrieve()
                .bodyToMono(AuthorDto[].class)
                .block();

        assertThat(response).isNotNull();
        assertThat(response.length).isGreaterThanOrEqualTo(2);
    }

    @Test
    void testUpdateAuthor() {
        Author author = authorRepository.save(new Author(null, "Alice", "Smith", "Biography of Alice Smith"));
        AuthorDto updatedAuthor = new AuthorDto(author.getAuthorId(), "Updated", "Name", "Updated Biography");

        AuthorDto response = webClient.put()
                .uri("/" + author.getAuthorId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateTestToken())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updatedAuthor)
                .retrieve()
                .bodyToMono(AuthorDto.class)
                .block();

        assertThat(response).isNotNull();
        assertThat(response.getFirstName()).isEqualTo("Updated");
    }

    @Test
    void testDeleteAuthor() {
        Author author = authorRepository.save(new Author(null, "Bob", "Johnson", "Biography of Bob Johnson"));

        webClient.delete()
                .uri("/" + author.getAuthorId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateTestToken())
                .retrieve()
                .bodyToMono(Void.class)
                .block();

        assertThat(authorRepository.findById(author.getAuthorId())).isEmpty();
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
