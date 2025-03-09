package com.library.IT;

import com.library.dto.BookDto;
import com.library.dto.ReservationDto;
import com.library.dto.UserDto;
import com.library.entity.*;
import com.library.mapper.BookMapper;
import com.library.mapper.ReservationMapper;
import com.library.mapper.UserMapper;
import com.library.repository.BookRepository;
import com.library.repository.ReservationRepository;
import com.library.repository.UserRepository;
import com.library.service.BookService;
import com.library.service.ReservationService;
import com.library.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ExtendWith(SpringExtension.class)
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ReservationServiceIT {

    @Autowired
    private ReservationService reservationService;
    @Autowired
    private UserService userService;
    @Autowired
    private BookService bookService;
    @Autowired
    private ReservationMapper reservationMapper;
    @Autowired
    private BookMapper bookMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private UserRepository userRepository;

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16.0")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @BeforeAll
    static void setup() {
        System.setProperty("spring.datasource.url", postgres.getJdbcUrl());
        System.setProperty("spring.datasource.username", postgres.getUsername());
        System.setProperty("spring.datasource.password", postgres.getPassword());
    }

    @AfterEach
    public void cleanUp() {
        reservationRepository.deleteAll();
        reservationRepository.flush();
        bookRepository.deleteAll();
        bookRepository.flush();
        userRepository.deleteAll();
        userRepository.flush();
    }

    private UserDto createTestUser(String username) {
        User user = User.builder()
                .email(username + "@test.com")
                .username(username)
                .password("password")
                .role(Role.ROLE_USER)
                .build();
        return userService.addUser(userMapper.toDto(user));
    }

    private BookDto createTestBook(String title) {
        Book book = Book.builder()
                .title(title)
                .description("Test Description")
                .availableCopies(5)
                .totalCopies(10)
                .build();
        return bookService.addBook(bookMapper.toDto(book));
    }

    @Test
    void addReservation_ShouldSaveAndReturnReservationDto() {
        UserDto user = createTestUser("testUser1");
        BookDto book = createTestBook("testBook1");

        Reservation reservation = new Reservation(null,
                userMapper.toEntity(user),
                bookMapper.toEntity(book),
                LocalDateTime.now(),
                ReservationStatus.CONFIRMED);

        ReservationDto savedReservation = reservationService.addReservation(reservationMapper.toDto(reservation));

        assertNotNull(savedReservation);
        assertNotNull(savedReservation.getReservationId());
        assertEquals(user.getUserId(), savedReservation.getUserId());
        assertEquals(book.getBookId(), savedReservation.getBookId());
    }

    @Test
    void getReservationById_ShouldReturnReservationDto() {
        UserDto user = createTestUser("testUser2");
        BookDto book = createTestBook("testBook2");

        Reservation reservation = new Reservation(null,
                userMapper.toEntity(user),
                bookMapper.toEntity(book),
                LocalDateTime.now(),
                ReservationStatus.CONFIRMED);

        ReservationDto savedReservation = reservationService.addReservation(reservationMapper.toDto(reservation));

        ReservationDto foundReservation = reservationService.getReservationById(savedReservation.getReservationId());

        assertNotNull(foundReservation);
        assertEquals(savedReservation.getReservationId(), foundReservation.getReservationId());
    }

    @Test
    void getAllReservations_ShouldReturnListOfReservationDto() {
        UserDto user1 = createTestUser("user1");
        UserDto user2 = createTestUser("user2");
        BookDto book1 = createTestBook("book1");
        BookDto book2 = createTestBook("book2");

        Reservation reservation1 = new Reservation(null,
                userMapper.toEntity(user1),
                bookMapper.toEntity(book1),
                LocalDateTime.now(),
                ReservationStatus.CONFIRMED);

        Reservation reservation2 = new Reservation(null,
                userMapper.toEntity(user2),
                bookMapper.toEntity(book2),
                LocalDateTime.now(),
                ReservationStatus.CONFIRMED);

        reservationService.addReservation(reservationMapper.toDto(reservation1));
        reservationService.addReservation(reservationMapper.toDto(reservation2));

        List<ReservationDto> reservations = reservationService.getAllReservations();

        assertTrue(reservations.size() >= 2);
    }

    @Test
    void updateReservation_ShouldUpdateAndReturnReservationDto() {
        UserDto user = createTestUser("testUser");
        BookDto book = createTestBook("testBook");

        Reservation reservation = new Reservation(null,
                userMapper.toEntity(user),
                bookMapper.toEntity(book),
                LocalDateTime.now(),
                ReservationStatus.CONFIRMED);

        ReservationDto savedReservation = reservationService.addReservation(reservationMapper.toDto(reservation));
        savedReservation.setStatus("CANCELLED");

        ReservationDto updatedReservation = reservationService.updateReservation(savedReservation.getReservationId(), savedReservation);

        assertNotNull(updatedReservation);
        assertEquals("CANCELLED", updatedReservation.getStatus());
    }

    @Test
    void deleteReservation_ShouldRemoveReservation() {
        UserDto user = createTestUser("testUser");
        BookDto book = createTestBook("testBook");

        Reservation reservation = new Reservation(null,
                userMapper.toEntity(user),
                bookMapper.toEntity(book),
                LocalDateTime.now(),
                ReservationStatus.CONFIRMED);
        ReservationDto savedReservation = reservationService.addReservation(reservationMapper.toDto(reservation));

        reservationService.deleteReservation(savedReservation.getReservationId());

        assertThrows(NoSuchElementException.class, () -> reservationService.getReservationById(savedReservation.getReservationId()));
    }

    @Test
    void reserveBook_ShouldCreateReservationForUserAndBook() {
        UserDto user = createTestUser("reserveUser");
        BookDto book = createTestBook("reserveBook");

        ReservationDto reservation = reservationService.reserveBook(user.getUserId(), book.getBookId());

        assertNotNull(reservation);
        assertEquals(user.getUserId(), reservation.getUserId());
        assertEquals(book.getBookId(), reservation.getBookId());
        assertEquals("CONFIRMED", reservation.getStatus());
    }

    @Test
    void cancelReservation_ShouldDeleteExistingReservation() {
        UserDto user = createTestUser("cancelUser");
        BookDto book = createTestBook("cancelBook");

        ReservationDto reservation = reservationService.reserveBook(user.getUserId(), book.getBookId());
        assertNotNull(reservation);

        reservationService.cancelReservation(reservation.getReservationId());

        assertThrows(NoSuchElementException.class, () -> reservationService.getReservationById(reservation.getReservationId()));
    }

    @Test
    void getReservationsByUser_ShouldReturnUserReservations() {
        UserDto user = createTestUser("userReservations");
        BookDto book1 = createTestBook("bookOne");
        BookDto book2 = createTestBook("bookTwo");

        reservationService.reserveBook(user.getUserId(), book1.getBookId());
        reservationService.reserveBook(user.getUserId(), book2.getBookId());

        List<ReservationDto> reservations = reservationService.getReservationsByUser(user.getUserId());

        assertEquals(2, reservations.size());
        assertEquals(user.getUserId(), reservations.get(0).getUserId());
        assertEquals(user.getUserId(), reservations.get(1).getUserId());
    }
}
