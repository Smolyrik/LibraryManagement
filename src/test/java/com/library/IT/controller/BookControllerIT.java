package com.library.IT.controller;

import com.library.dto.BookDto;
import com.library.entity.Book;
import com.library.entity.Role;
import com.library.entity.User;
import com.library.repository.BookRepository;
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
class BookControllerIT {

    @LocalServerPort
    private int port;

    private WebClient webClient;

    @Autowired
    private BookRepository bookRepository;

    @Value("${token.signing.key}")
    private String jwtSigningKey;

    private Book book;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        String baseUrl = "http://localhost:" + port + "/api/books";
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();

        userRepository.save(new User(null, "Test User", "test@example.com", "hashedpassword", Role.ROLE_ADMIN));

        book = bookRepository.save(new Book(null, "Test Book", "Description", 5, 10));
    }

    @AfterEach
    void cleanUp() {
        bookRepository.deleteAll();
        bookRepository.flush();

        userRepository.deleteAll();
        userRepository.flush();
    }

    @Test
    void testAddBook() {
        BookDto bookDto = new BookDto(null, "New Book", "New Description", 3, 7);

        BookDto response = webClient.post()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateTestToken())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(bookDto)
                .retrieve()
                .bodyToMono(BookDto.class)
                .block();

        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo("New Book");
    }

    @Test
    void testGetBookById() {
        BookDto response = webClient.get()
                .uri("/" + book.getBookId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateTestToken())
                .retrieve()
                .bodyToMono(BookDto.class)
                .block();

        assertThat(response).isNotNull();
        assertThat(response.getBookId()).isEqualTo(book.getBookId());
    }

    @Test
    void testGetAllBooks() {
        BookDto[] response = webClient.get()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateTestToken())
                .retrieve()
                .bodyToMono(BookDto[].class)
                .block();

        assertThat(response).isNotNull();
        assertThat(response.length).isGreaterThanOrEqualTo(1);
    }

    @Test
    void testUpdateBook() {
        BookDto updatedBookDto = new BookDto(null, "Updated Book", "Updated Description", 8, 15);

        BookDto response = webClient.put()
                .uri("/" + book.getBookId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateTestToken())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updatedBookDto)
                .retrieve()
                .bodyToMono(BookDto.class)
                .block();

        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo("Updated Book");
    }

    @Test
    void testDeleteBook() {
        webClient.delete()
                .uri("/" + book.getBookId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateTestToken())
                .retrieve()
                .bodyToMono(Void.class)
                .block();

        assertThat(bookRepository.findById(book.getBookId())).isEmpty();
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
