package com.library.IT.controller;

import com.library.dto.CategoryDto;
import com.library.entity.Category;
import com.library.entity.Role;
import com.library.entity.User;
import com.library.repository.CategoryRepository;
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
class CategoryControllerIT {

    @LocalServerPort
    private int port;

    private WebClient webClient;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Value("${token.signing.key}")
    private String jwtSigningKey;

    @BeforeEach
    void setUp() {
        String baseUrl = "http://localhost:" + port + "/api/categories";
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();

        userRepository.save(new User(null, "Test User", "test@example.com", "hashedpassword", Role.ROLE_ADMIN));
    }

    @AfterEach
    void cleanUp() {
        userRepository.deleteAll();
        userRepository.flush();

        categoryRepository.deleteAll();
        categoryRepository.flush();
    }

    @Test
    void testAddCategory() {
        CategoryDto categoryDto = new CategoryDto(null, "Fiction", "Fictional books");

        CategoryDto response = webClient.post()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateTestToken())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(categoryDto)
                .retrieve()
                .bodyToMono(CategoryDto.class)
                .block();

        assertThat(response).isNotNull();
        assertThat(response.getCategoryName()).isEqualTo("Fiction");
    }

    @Test
    void testGetCategoryById() {
        Category category = categoryRepository.save(new Category(null, "Science", "Scientific books"));

        CategoryDto response = webClient.get()
                .uri("/" + category.getCategoryId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateTestToken())
                .retrieve()
                .bodyToMono(CategoryDto.class)
                .block();

        assertThat(response).isNotNull();
        assertThat(response.getCategoryName()).isEqualTo("Science");
    }

    @Test
    void testGetAllCategories() {
        categoryRepository.save(new Category(null, "History", "Historical books"));
        categoryRepository.save(new Category(null, "Fantasy", "Fantasy books"));

        CategoryDto[] response = webClient.get()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateTestToken())
                .retrieve()
                .bodyToMono(CategoryDto[].class)
                .block();

        assertThat(response).isNotNull();
        assertThat(response.length).isGreaterThanOrEqualTo(2);
    }

    @Test
    void testUpdateCategory() {
        Category category = categoryRepository.save(new Category(null, "Romance", "Romantic books"));
        CategoryDto updatedCategory = new CategoryDto(category.getCategoryId(), "Updated", "Updated Description");

        CategoryDto response = webClient.put()
                .uri("/" + category.getCategoryId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateTestToken())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updatedCategory)
                .retrieve()
                .bodyToMono(CategoryDto.class)
                .block();

        assertThat(response).isNotNull();
        assertThat(response.getCategoryName()).isEqualTo("Updated");
    }

    @Test
    void testDeleteCategory() {
        Category category = categoryRepository.save(new Category(null, "Horror", "Horror books"));

        webClient.delete()
                .uri("/" + category.getCategoryId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateTestToken())
                .retrieve()
                .bodyToMono(Void.class)
                .block();

        assertThat(categoryRepository.findById(category.getCategoryId())).isEmpty();
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
