package com.library.IT.controller;

import com.library.dto.LoanDto;
import com.library.entity.*;
import com.library.repository.BookRepository;
import com.library.repository.LoanRepository;
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
class LoanControllerIT {

    @LocalServerPort
    private int port;

    private WebClient webClient;

    @Autowired
    private LoanRepository loanRepository;

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
        String baseUrl = "http://localhost:" + port + "/api/loans";
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();

        testUser = userRepository.save(new User(null, "Test User", "test@example.com", "hashedpassword", Role.ROLE_USER));
        testBook = bookRepository.save(new Book(null, "Test Book", "description", 10, 20));
    }

    @AfterEach
    void cleanUp() {
        loanRepository.deleteAll();
        loanRepository.flush();

        bookRepository.deleteAll();
        bookRepository.flush();

        userRepository.deleteAll();
        userRepository.flush();
    }

    @Test
    void testCreateLoan() {
        LoanDto loanDto = new LoanDto(null, "ACTIVE", LocalDateTime.now(), LocalDateTime.now().plusDays(7), testUser.getUserId(), testBook.getBookId());

        LoanDto response = webClient.post()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateTestToken())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(loanDto)
                .retrieve()
                .bodyToMono(LoanDto.class)
                .block();

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo("ACTIVE");
    }

    @Test
    void testGetLoanById() {
        Loan loan = loanRepository.save(new Loan(null, LoanStatus.ACTIVE, LocalDateTime.now(), LocalDateTime.now().plusDays(7), testUser, testBook));

        LoanDto response = webClient.get()
                .uri("/" + loan.getLoanId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateTestToken())
                .retrieve()
                .bodyToMono(LoanDto.class)
                .block();

        assertThat(response).isNotNull();
        assertThat(response.getLoanId()).isEqualTo(loan.getLoanId());
    }

    @Test
    void testGetAllLoans() {
        loanRepository.save(new Loan(null, LoanStatus.ACTIVE, LocalDateTime.now(), LocalDateTime.now().plusDays(7), testUser, testBook));
        loanRepository.save(new Loan(null, LoanStatus.RETURNED, LocalDateTime.now(), LocalDateTime.now().plusDays(7), testUser, testBook));

        LoanDto[] response = webClient.get()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateTestToken())
                .retrieve()
                .bodyToMono(LoanDto[].class)
                .block();

        assertThat(response).isNotNull();
        assertThat(response.length).isGreaterThanOrEqualTo(2);
    }

    @Test
    void testUpdateLoan() {
        Loan loan = loanRepository.save(new Loan(null, LoanStatus.ACTIVE, LocalDateTime.now(), LocalDateTime.now().plusDays(7), testUser, testBook));
        LoanDto updatedLoan = new LoanDto(loan.getLoanId(), "RETURNED", loan.getLoanDate(), LocalDateTime.now().plusDays(10), testUser.getUserId(), testBook.getBookId());

        LoanDto response = webClient.put()
                .uri("/" + loan.getLoanId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateTestToken())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updatedLoan)
                .retrieve()
                .bodyToMono(LoanDto.class)
                .block();

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo("RETURNED");
    }

    @Test
    void testDeleteLoan() {
        Loan loan = loanRepository.save(new Loan(null, LoanStatus.ACTIVE, LocalDateTime.now(), LocalDateTime.now().plusDays(7), testUser, testBook));

        webClient.delete()
                .uri("/" + loan.getLoanId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + generateTestToken())
                .retrieve()
                .bodyToMono(Void.class)
                .block();

        assertThat(loanRepository.findById(loan.getLoanId())).isEmpty();
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
