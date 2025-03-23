package com.library.IT.controller;

import com.library.dto.BookAuthorDto;
import com.library.entity.*;
import com.library.repository.BookRepository;
import com.library.repository.AuthorRepository;
import com.library.repository.BookAuthorRepository;
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
class BookAuthorControllerIT {

    @LocalServerPort
    private int port;

    private WebClient webClient;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private BookAuthorRepository bookAuthorRepository;

    @Autowired
    private UserRepository userRepository;

    @Value("${token.signing.key}")
    private String jwtSigningKey;

    private Book book;
    private Author author;

    @BeforeEach
    void setUp() {
        String baseUrl = "http://localhost:" + port + "/api/book-authors";
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();

        userRepository.save(new User(null, "Test User", "test@example.com", "hashedpassword", Role.ROLE_ADMIN));

        book = bookRepository.save(new Book(null, "Test Book", "Description", 10, 20));
        author = authorRepository.save(new Author(null, "John", "Doe", "Biography"));
    }

    @AfterEach
    void cleanUp() {
        bookAuthorRepository.deleteAll();
        bookAuthorRepository.flush();

        bookRepository.deleteAll();
        bookRepository.flush();

        authorRepository.deleteAll();
        authorRepository.flush();

        userRepository.deleteAll();
        userRepository.flush();
    }

    @Test
    void testAddBookAuthor() {
        BookAuthorDto bookAuthorDto = new BookAuthorDto(book.getBookId(), author.getAuthorId());

        BookAuthorDto response = webClient.post()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateTestToken())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(bookAuthorDto)
                .retrieve()
                .bodyToMono(BookAuthorDto.class)
                .block();

        assertThat(response).isNotNull();
        assertThat(response.getBookId()).isEqualTo(book.getBookId());
        assertThat(response.getAuthorId()).isEqualTo(author.getAuthorId());
    }

    @Test
    void testGetBookAuthorById() {
        bookAuthorRepository.save(new BookAuthor(new BookAuthorId(book.getBookId(), author.getAuthorId()), book, author));

        BookAuthorDto response = webClient.get()
                .uri("/" + book.getBookId() + "/" + author.getAuthorId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateTestToken())
                .retrieve()
                .bodyToMono(BookAuthorDto.class)
                .block();

        assertThat(response).isNotNull();
        assertThat(response.getBookId()).isEqualTo(book.getBookId());
        assertThat(response.getAuthorId()).isEqualTo(author.getAuthorId());
    }

    @Test
    void testGetAllBookAuthors() {
        bookAuthorRepository.save(new BookAuthor(new BookAuthorId(book.getBookId(), author.getAuthorId()), book, author));

        BookAuthorDto[] response = webClient.get()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateTestToken())
                .retrieve()
                .bodyToMono(BookAuthorDto[].class)
                .block();

        assertThat(response).isNotNull();
        assertThat(response.length).isGreaterThanOrEqualTo(1);
    }

    @Test
    void testDeleteBookAuthor() {
        bookAuthorRepository.save(new BookAuthor(new BookAuthorId(book.getBookId(), author.getAuthorId()), book, author));

        webClient.delete()
                .uri("/" + book.getBookId() + "/" + author.getAuthorId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateTestToken())
                .retrieve()
                .bodyToMono(Void.class)
                .block();

        assertThat(bookAuthorRepository.findById(new BookAuthorId(book.getBookId(), author.getAuthorId()))).isEmpty();
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
