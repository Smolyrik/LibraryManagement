package com.library.IT.controller;

import com.library.dto.ReservationDto;
import com.library.entity.*;
import com.library.repository.BookRepository;
import com.library.repository.ReservationRepository;
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
class ReservationControllerIT {

    @LocalServerPort
    private int port;

    private WebClient webClient;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Value("${token.signing.key}")
    private String jwtSigningKey;

    private User testUser;
    private Book testBook;

    @BeforeEach
    void setUp() {
        String baseUrl = "http://localhost:" + port + "/api/reservations";
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();

        testUser = userRepository.save(new User(null, "Test User", "test@example.com", "hashedpassword", Role.ROLE_USER));
        testBook = bookRepository.save(new Book(null, "Test Book", "description", 10, 20));
    }

    @AfterEach
    void cleanUp() {
        reservationRepository.deleteAll();
        reservationRepository.flush();

        bookRepository.deleteAll();
        bookRepository.flush();

        userRepository.deleteAll();
        userRepository.flush();
    }

    @Test
    void testCreateReservation() {
        ReservationDto reservationDto = new ReservationDto(null, testUser.getUserId(), testBook.getBookId(), LocalDateTime.now(), "PENDING");

        ReservationDto response = webClient.post()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateTestToken())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(reservationDto)
                .retrieve()
                .bodyToMono(ReservationDto.class)
                .block();

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo("PENDING");
    }

    @Test
    void testGetReservationById() {
        Reservation reservation = reservationRepository.save(new Reservation(null, testUser, testBook, LocalDateTime.now(), ReservationStatus.PENDING));

        ReservationDto response = webClient.get()
                .uri("/" + reservation.getReservationId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateTestToken())
                .retrieve()
                .bodyToMono(ReservationDto.class)
                .block();

        assertThat(response).isNotNull();
        assertThat(response.getReservationId()).isEqualTo(reservation.getReservationId());
    }

    @Test
    void testGetAllReservations() {
        reservationRepository.save(new Reservation(null, testUser, testBook, LocalDateTime.now(), ReservationStatus.PENDING));
        reservationRepository.save(new Reservation(null, testUser, testBook, LocalDateTime.now(), ReservationStatus.CONFIRMED));

        ReservationDto[] response = webClient.get()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateTestToken())
                .retrieve()
                .bodyToMono(ReservationDto[].class)
                .block();

        assertThat(response).isNotNull();
        assertThat(response.length).isGreaterThanOrEqualTo(2);
    }

    @Test
    void testUpdateReservation() {
        Reservation reservation = reservationRepository.save(new Reservation(null, testUser, testBook, LocalDateTime.now(), ReservationStatus.PENDING));
        ReservationDto updatedReservation = new ReservationDto(reservation.getReservationId(), testUser.getUserId(), testBook.getBookId(), reservation.getReservationTime(), "CONFIRMED");

        ReservationDto response = webClient.put()
                .uri("/" + reservation.getReservationId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateTestToken())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updatedReservation)
                .retrieve()
                .bodyToMono(ReservationDto.class)
                .block();

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo("CONFIRMED");
    }

    @Test
    void testDeleteReservation() {
        Reservation reservation = reservationRepository.save(new Reservation(null, testUser, testBook, LocalDateTime.now(), ReservationStatus.PENDING));

        webClient.delete()
                .uri("/" + reservation.getReservationId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateTestToken())
                .retrieve()
                .bodyToMono(Void.class)
                .block();

        assertThat(reservationRepository.findById(reservation.getReservationId())).isEmpty();
    }

    private String generateTestToken() {
        Key key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSigningKey));

        return Jwts.builder()
                .subject("Test User")
                .claim("authorities", List.of("ROLE_USER"))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(key)
                .compact();
    }
}
