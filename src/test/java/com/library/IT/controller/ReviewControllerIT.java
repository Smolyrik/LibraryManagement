package com.library.IT.controller;

import com.library.dto.ReviewDto;
import com.library.entity.Book;
import com.library.entity.Review;
import com.library.entity.Role;
import com.library.entity.User;
import com.library.repository.BookRepository;
import com.library.repository.ReviewRepository;
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
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ReviewControllerIT {

    @LocalServerPort
    private int port;

    private WebClient webClient;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Value("${token.signing.key}")
    private String jwtSigningKey;

    private User testUser;
    private Book testBook;
    @Autowired
    private BookRepository bookRepository;

    @BeforeEach
    void setUp() {
        String baseUrl = "http://localhost:" + port + "/api/reviews";
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();

        testUser = userRepository.save(new User(null, "Test User", "test@example.com", "hashedpassword", Role.ROLE_ADMIN));
        testBook = bookRepository.save(new Book(null, "Test Title", "description", 10, 20));
    }

    @AfterEach
    void cleanUp() {
        reviewRepository.deleteAll();
        reviewRepository.flush();

        bookRepository.deleteAll();
        bookRepository.flush();

        userRepository.deleteAll();
        userRepository.flush();
    }

    @Test
    void testAddReview() {
        ReviewDto reviewDto = new ReviewDto(null, testUser.getUserId(), testBook.getBookId(), "Great book!", (short) 9, LocalDateTime.now());

        ReviewDto response = webClient.post()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateTestToken())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(reviewDto)
                .retrieve()
                .bodyToMono(ReviewDto.class)
                .block();

        assertThat(response).isNotNull();
        assertThat(response.getComment()).isEqualTo("Great book!");
    }

    @Test
    void testGetReviewById() {
        Review review = reviewRepository.save(new Review(null, testUser, testBook, "Nice read", (short) 8, LocalDateTime.now()));

        ReviewDto response = webClient.get()
                .uri("/" + review.getReviewId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateTestToken())
                .retrieve()
                .bodyToMono(ReviewDto.class)
                .block();

        assertThat(response).isNotNull();
        assertThat(response.getComment()).isEqualTo("Nice read");
    }

    @Test
    void testGetAllReviews() {
        reviewRepository.save(new Review(null, testUser, testBook, "Awesome!", (short) 10, LocalDateTime.now()));
        reviewRepository.save(new Review(null, testUser, testBook, "Good book", (short) 7, LocalDateTime.now()));

        ReviewDto[] response = webClient.get()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateTestToken())
                .retrieve()
                .bodyToMono(ReviewDto[].class)
                .block();

        assertThat(response).isNotNull();
        assertThat(response.length).isGreaterThanOrEqualTo(2);
    }

    @Test
    void testUpdateReview() {
        Review review = reviewRepository.save(new Review(null, testUser, testBook, "Nice book", (short) 7, LocalDateTime.now()));
        ReviewDto updatedReview = new ReviewDto(review.getReviewId(), testUser.getUserId(), testBook.getBookId(), "Updated comment", (short) 9, LocalDateTime.now());

        ReviewDto response = webClient.put()
                .uri("/" + review.getReviewId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateTestToken())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updatedReview)
                .retrieve()
                .bodyToMono(ReviewDto.class)
                .block();

        assertThat(response).isNotNull();
        assertThat(response.getComment()).isEqualTo("Updated comment");
    }

    @Test
    void testDeleteReview() {
        Review review = reviewRepository.save(new Review(null, testUser, testBook, "To be deleted", (short) 5, LocalDateTime.now()));

        webClient.delete()
                .uri("/" + review.getReviewId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateTestToken())
                .retrieve()
                .bodyToMono(Void.class)
                .block();

        assertThat(reviewRepository.findById(review.getReviewId())).isEmpty();
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

