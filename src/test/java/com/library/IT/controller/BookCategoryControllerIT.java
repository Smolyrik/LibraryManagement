package com.library.IT.controller;

import com.library.dto.BookCategoryDto;
import com.library.entity.*;
import com.library.repository.BookRepository;
import com.library.repository.CategoryRepository;
import com.library.repository.BookCategoryRepository;
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
class BookCategoryControllerIT {

    @LocalServerPort
    private int port;

    private WebClient webClient;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BookCategoryRepository bookCategoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Value("${token.signing.key}")
    private String jwtSigningKey;

    private Book book;
    private Category category;

    @BeforeEach
    void setUp() {
        String baseUrl = "http://localhost:" + port + "/api/book-categories";
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();

        userRepository.save(new User(null, "Test User", "test@example.com", "hashedpassword", Role.ROLE_ADMIN));

        book = bookRepository.save(new Book(null, "Test Book", "Description", 10, 20));
        category = categoryRepository.save(new Category(null, "Fiction", "description"));
    }

    @AfterEach
    void cleanUp() {
        bookCategoryRepository.deleteAll();
        bookCategoryRepository.flush();

        bookRepository.deleteAll();
        bookRepository.flush();

        categoryRepository.deleteAll();
        categoryRepository.flush();

        userRepository.deleteAll();
        userRepository.flush();
    }

    @Test
    void testAddBookCategory() {
        BookCategoryDto bookCategoryDto = new BookCategoryDto(book.getBookId(), category.getCategoryId());

        BookCategoryDto response = webClient.post()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateTestToken())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(bookCategoryDto)
                .retrieve()
                .bodyToMono(BookCategoryDto.class)
                .block();

        assertThat(response).isNotNull();
        assertThat(response.getBookId()).isEqualTo(book.getBookId());
        assertThat(response.getCategoryId()).isEqualTo(category.getCategoryId());
    }

    @Test
    void testGetBookCategoryById() {
        bookCategoryRepository.save(new BookCategory(new BookCategoryId(book.getBookId(), category.getCategoryId()), book, category));

        BookCategoryDto response = webClient.get()
                .uri("/" + book.getBookId() + "/" + category.getCategoryId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateTestToken())
                .retrieve()
                .bodyToMono(BookCategoryDto.class)
                .block();

        assertThat(response).isNotNull();
        assertThat(response.getBookId()).isEqualTo(book.getBookId());
        assertThat(response.getCategoryId()).isEqualTo(category.getCategoryId());
    }

    @Test
    void testGetAllBookCategories() {
        bookCategoryRepository.save(new BookCategory(new BookCategoryId(book.getBookId(), category.getCategoryId()), book, category));

        BookCategoryDto[] response = webClient.get()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateTestToken())
                .retrieve()
                .bodyToMono(BookCategoryDto[].class)
                .block();

        assertThat(response).isNotNull();
        assertThat(response.length).isGreaterThanOrEqualTo(1);
    }

    @Test
    void testDeleteBookCategory() {
        bookCategoryRepository.save(new BookCategory(new BookCategoryId(book.getBookId(), category.getCategoryId()), book, category));

        webClient.delete()
                .uri("/" + book.getBookId() + "/" + category.getCategoryId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateTestToken())
                .retrieve()
                .bodyToMono(Void.class)
                .block();

        assertThat(bookCategoryRepository.findById(new BookCategoryId(book.getBookId(), category.getCategoryId()))).isEmpty();
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
